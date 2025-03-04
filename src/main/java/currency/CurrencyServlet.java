package currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.classes.Currency;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(urlPatterns = {"/currency/*", "/currencies"})
public class CurrencyServlet extends HttpServlet {

    CurrencyDAO currencyDAO = new CurrencyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String json;

            String servletPath = request.getServletPath();

            if ("/currencies".equals(servletPath)) {
                ArrayList<Currency> currencies = currencyDAO.getAllCurrencies();

                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencies);
                PrintWriter out = response.getWriter();
                out.print(json);
                response.setStatus(200);
                out.flush();

            }
            else if ("/currency".equals(servletPath)){
                String pathInfo = request.getPathInfo();
                if (pathInfo == null || pathInfo.equals("/")) {
                    json = objectMapper.writeValueAsString("Код валюты отсутствует в адресе");
                    response.setStatus(400);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }
                pathInfo = pathInfo.substring(1);
                Currency currency = currencyDAO.getCurrency(pathInfo);

                if (currency == null) {
                    json = objectMapper.writeValueAsString("Валюта не найдена");
                    response.setStatus(404);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currency);
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
            throws ServletException, IOException{

        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String json;

            String servletPath = request.getServletPath();
            if ("/currencies".equals(servletPath)) {

                String name = request.getParameter("name");
                String sign = request.getParameter("sign");
                String code = request.getParameter("code");

                if (name == null || sign == null || code == null) {
                    json = objectMapper.writeValueAsString("Отсутствует нужное поле формы");
                    response.setStatus(400);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                Currency currency = currencyDAO.setCurrency(code, name, sign);

                if (currency == null) {
                    json = objectMapper.writeValueAsString("Валюта с таким кодом уже существует");
                    response.setStatus(409);
                    PrintWriter msg = response.getWriter();
                    msg.print(json);
                    msg.flush();
                    return;
                }

                json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currency);
                PrintWriter out = response.getWriter();
                out.print(json);
                response.setStatus(201);
                out.flush();
            }
        } catch (IOException e) {
            response.sendError(500, e.getMessage());
        }
    }
}