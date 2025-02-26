package exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.classes.Exchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();

        String path = request.getServletPath();

        if ("/exchangeRate".equals(path)) {

            Exchange exchange = null;

            String pathInfo = request.getPathInfo().substring(1);;
            String base = pathInfo.substring(0, 3);
            String target = pathInfo.substring(3, 6);

            if (base.compareToIgnoreCase(target) < 0) {
                exchange = exchangeDAO.getExchangeRate(base, target);
            } else if (base.compareToIgnoreCase(target) > 0) {
                exchange = exchangeDAO.getExchangeRate(target, base);
            } else {
                // Слова одинаковые. Кидаю ошибку
            }

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchange);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        }
        else if ("/exchangeRates".equals(path)) {
            ArrayList<Exchange> exchanges = exchangeDAO.getAllExchangeRates();

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchanges);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();

        String path = request.getServletPath();

        if ("/exchangeRates".equals(path)) {

            Exchange exchange = null;

            String baseCurrencyCode = request.getParameter("baseCurrencyCode");
            String targetCurrencyCode = request.getParameter("targetCurrencyCode");
            double rate = Double.parseDouble(request.getParameter("rate"));

            if (baseCurrencyCode.compareToIgnoreCase(targetCurrencyCode) < 0) {
                exchange = exchangeDAO.setExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            } else if (baseCurrencyCode.compareToIgnoreCase(targetCurrencyCode) > 0) {
                exchange = exchangeDAO.setExchangeRate(targetCurrencyCode, baseCurrencyCode, 1/rate);
            } else {
                // Слова одинаковые. Кидаю ошибку
            }

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchange);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();

        String servletPath = request.getServletPath();

        if ("/exchangeRate".equals(servletPath)) {

            Exchange exchange = null;
            String pathInfo = request.getPathInfo().substring(1);

            String base = pathInfo.substring(0, 3);
            String target = pathInfo.substring(3, 6);
            double rate = catchRate(request);

            if (base.compareToIgnoreCase(target) < 0) {
                exchange = exchangeDAO.updateExchangeRate(base, target, rate);
            } else if (base.compareToIgnoreCase(target) > 0) {
                exchange = exchangeDAO.updateExchangeRate(target, base, 1/rate);
            } else {
                // Слова одинаковые. Кидаю ошибку
            }

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchange);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        }
    }

    private double catchRate(HttpServletRequest request) throws IOException {
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
    }
}