package exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.classes.Exchange;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
            Exchange exchange = exchangeDAO.getExchangeRate("USD", "RUB");

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchange);
            PrintWriter out = response.getWriter();
            out.print(json);
            out.flush();
        }
        else if ("/exchangeRates".equals(path)) {
            ArrayList<Exchange> exchanges = exchangeDAO.getAllExchangeRates();

            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchanges);
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
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

        String path = request.getServletPath();

        if ("/exchangeRates".equals(path)) {
//            exchangeDAO.setExchangeRate("EUR", "USD", 1.5);

//            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchange);
//            PrintWriter out = response.getWriter();
//            out.print(json);
//            out.flush();
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String path = request.getServletPath();

        if ("/exchangeRate".equals(path)) {
//            Exchange exchange = exchangeDAO.updateExchangeRate("USD", "RUB", 90);

//            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchange);
//            PrintWriter out = response.getWriter();
//            out.print(json);
//            out.flush();
        }
    }

    private void temp(String targetCurrency, String baseCurrency) {
        if (targetCurrency.compareToIgnoreCase(baseCurrency) < 0) {
            // Делаю что то
        } else if (targetCurrency.compareToIgnoreCase(baseCurrency) > 0) {
            // Делаю то же самое, но меняю слова местами
        } else {
            // Слова одинаковые. Кидаю ошибку
        }
    }
}
