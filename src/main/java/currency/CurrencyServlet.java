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

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();

        String servletPath = request.getServletPath();

        if ("/currencies".equals(servletPath)) {
            ArrayList<Currency> currencies = currencyDAO.getAllCurrencies();

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currencies);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();

        }
        else if ("/currency".equals(servletPath)){
            String pathInfo = request.getPathInfo().substring(1);
            Currency currency = currencyDAO.getCurrency(pathInfo);

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currency);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        }


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();

        String servletPath = request.getServletPath();
        if ("/currencies".equals(servletPath)) {

            String name = request.getParameter("name");
            String sign = request.getParameter("sign");
            String code = request.getParameter("code");

            Currency currency = currencyDAO.setCurrency(code, name, sign);

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(currency);
            PrintWriter out = response.getWriter();
            response.setStatus(200);
            out.print(json);
            out.flush();
        }
    }
}