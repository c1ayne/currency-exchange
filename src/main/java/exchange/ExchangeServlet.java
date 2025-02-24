package exchange;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {"/exchangeRate/*", "/exchangeRates"})
public class ExchangeServlet extends HttpServlet {

    ExchangeDAO exchangeDAO = new ExchangeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/exchangeRate".equals(path)) {
            exchangeDAO.getExchangeRate("USD", "RUB");
        }
        else if ("/exchangeRates".equals(path)) {
//            exchangeDAO.getAllExchangeRates();
            exchangeDAO.setExchangeRate("EUR", "USD", 1.5);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/exchangeRates".equals(path)) {

        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if ("/exchangeRate".equals(path)) {

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
