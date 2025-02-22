package currency;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.classes.Currency;

import java.io.IOException;
import java.io.PrintWriter;

//@WebServlet("/currencies")
@WebServlet(urlPatterns = {"/currency/*", "/currencies"})
public class CurrencyServlet extends HttpServlet {

    CurrencyDAO currencyDAO = new CurrencyDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PrintWriter writer = response.getWriter();

        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if ("/currencies".equals(servletPath)) {
            Currency currency = currencyDAO.getCurrency();
//            writer.println();
//            writer.println();
//            writer.println();
//            writer.println();

            System.out.println(currency.getId());
            System.out.println(currency.getCode());
            System.out.println(currency.getFullName());
            System.out.println(currency.getSign());
        }


    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

    }
}
