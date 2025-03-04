package exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import currency.CurrencyDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.classes.Exchange;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static java.net.URLDecoder.decode;

@WebServlet(urlPatterns = {"/exchangeRate/*", "/exchangeRates"})
public class ExchangeServlet extends HttpServlet {

    ExchangeDAO exchangeDAO = new ExchangeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String json;

            String path = request.getServletPath();

            if ("/exchangeRate".equals(path)) {

                Exchange exchange;

                String pathInfo = request.getPathInfo();
                if (pathInfo == null || pathInfo.substring(1).length() != 6) {
                    json = objectMapper.writeValueAsString("Коды валют отсутствуют или введены неверно");
                    response.setStatus(400);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }
                pathInfo = pathInfo.substring(1);
                String base = pathInfo.substring(0, 3);
                String target = pathInfo.substring(3, 6);

                if (base.compareToIgnoreCase(target) < 0) {
                    exchange = exchangeDAO.getExchangeRate(base, target);
                } else if (base.compareToIgnoreCase(target) > 0) {
                    exchange = exchangeDAO.getExchangeRate(target, base);
                } else {
                    json = objectMapper.writeValueAsString("Вы ввели одинаковые валюты");
                    response.setStatus(400);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                if (exchange == null) {
                    json = objectMapper.writeValueAsString("Обменный курс для валюты не найден");
                    response.setStatus(404);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchange);
                PrintWriter out = response.getWriter();
                out.print(json);
                out.flush();
            }
            else if ("/exchangeRates".equals(path)) {
                ArrayList<Exchange> exchanges = exchangeDAO.getAllExchangeRates();

                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchanges);
                PrintWriter out = response.getWriter();
                out.print(json);
                response.setStatus(200);
                out.flush();
            }
        } catch (IOException e) {
            response.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            CurrencyDAO currencyDAO = new CurrencyDAO();
            String json;

            String path = request.getServletPath();

            if ("/exchangeRates".equals(path)) {

                Exchange exchange;

                String baseCurrencyCode = request.getParameter("baseCurrencyCode");
                String targetCurrencyCode = request.getParameter("targetCurrencyCode");

                if (baseCurrencyCode == null || targetCurrencyCode == null || request.getParameter("rate") == null) {
                    json = objectMapper.writeValueAsString("Отсутствует поле ввода");
                    response.setStatus(400);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                double rate;
                try {
                    rate = Double.parseDouble(request.getParameter("rate"));
                } catch (NumberFormatException e) {
                    json = objectMapper.writeValueAsString("rate должно быть double");
                    response.setStatus(400);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                if (currencyDAO.getCurrency(baseCurrencyCode) == null || currencyDAO.getCurrency(targetCurrencyCode) == null) {
                    json = objectMapper.writeValueAsString("Одна (или обе) валюта из валютной пары не существует в БД ");
                    response.setStatus(404);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                if (baseCurrencyCode.compareToIgnoreCase(targetCurrencyCode) < 0) {
                    exchange = exchangeDAO.setExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
                } else if (baseCurrencyCode.compareToIgnoreCase(targetCurrencyCode) > 0) {
                    exchange = exchangeDAO.setExchangeRate(targetCurrencyCode, baseCurrencyCode, 1/rate);
                } else {
                    json = objectMapper.writeValueAsString("Вы ввели две одинаковые валюты");
                    response.setStatus(409);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                if (exchange == null) {
                    json = objectMapper.writeValueAsString("Валютная пара с таким кодом уже существует");
                    response.setStatus(409);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchange);
                PrintWriter out = response.getWriter();
                out.print(json);
                response.setStatus(201);
                out.flush();
            }
        } catch (IOException e) {
            response.sendError(500, e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            ObjectMapper objectMapper = new ObjectMapper();

            String servletPath = request.getServletPath();

            if ("/exchangeRate".equals(servletPath)) {

                Exchange exchange;
                String json;

                String pathInfo = request.getPathInfo();

                if (pathInfo == null || pathInfo.substring(1).length() != 6) {
                    json = objectMapper.writeValueAsString("Отсутствует поле ввода");
                    response.setStatus(400);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                pathInfo = pathInfo.substring(1);
                String base = pathInfo.substring(0, 3);
                String target = pathInfo.substring(3, 6);
                double rate = catchRate(request);
                if (rate == 0) {
                    json = objectMapper.writeValueAsString("rate должно быть double");
                    response.setStatus(400);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                if (base.compareToIgnoreCase(target) < 0) {
                    if (exchangeDAO.getExchangeRate(base, target) == null) {
                        json = objectMapper.writeValueAsString("Валютной пары с таким кодом не существует");
                        response.setStatus(409);
                        PrintWriter msg = response.getWriter();
                        msg.print(json);
                        msg.flush();
                        return;
                    }
                    exchange = exchangeDAO.updateExchangeRate(base, target, rate);
                } else if (base.compareToIgnoreCase(target) > 0) {
                    if (exchangeDAO.getExchangeRate(target, base) == null) {
                        json = objectMapper.writeValueAsString("Валютной пары с таким кодом не существует");
                        response.setStatus(404);
                        PrintWriter msg = response.getWriter();
                        msg.print(json);
                        msg.flush();
                        return;
                    }
                    exchange = exchangeDAO.updateExchangeRate(target, base, 1/rate);
                } else {
                    json = objectMapper.writeValueAsString("Вы ввели две одинаковые валюты");
                    response.setStatus(404);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchange);
                PrintWriter out = response.getWriter();
                out.print(json);
                response.setStatus(200);
                out.flush();
            }
        } catch (IOException e) {
            response.sendError(500, e.getMessage());
        }
    }

    private double catchRate(HttpServletRequest request) throws IOException {
        try {
            String body = new BufferedReader(new InputStreamReader(request.getInputStream()))
                    .lines()
                    .collect(Collectors.joining("\n"));

            Map<String, String> parameters = Arrays.stream(body.split("&"))
                    .map(pair -> pair.split("="))
                    .collect(Collectors.toMap(
                            keyValue -> {
                                try {
                                    return decode(keyValue[0], "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                            },
                            keyValue -> {
                                try {
                                    return decode(keyValue[1], "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    ));
            return Double.parseDouble(parameters.get("rate"));
        } catch (Exception e) {
            return 0;
        }
    }
}