package currency;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.classes.Currency;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet(urlPatterns = {"/currency/*", "/currencies"})
public class CurrencyServlet extends HttpServlet {

    CurrencyDAO currencyDAO = new CurrencyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String servletPath = request.getServletPath();

        if ("/currencies".equals(servletPath)) {
            ArrayList<Currency> currencies = currencyDAO.getAllCurrencies();
//            for (Currency currency : currencies){
//                System.out.println(currency.getId());
//                System.out.println(currency.getCode());
//                System.out.println(currency.getFullName());
//                System.out.println(currency.getSign());
//            }
        }
        else if ("/currency".equals(servletPath)){
            String pathInfo = request.getPathInfo().substring(1);
            Currency currency = currencyDAO.getCurrency(pathInfo);
        }


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        String servletPath = request.getServletPath();
        if ("/currencies".equals(servletPath)) {
//            Currency currency = currencyDAO.setCurrency();
        }
    }
}