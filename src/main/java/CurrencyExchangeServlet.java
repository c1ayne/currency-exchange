import com.fasterxml.jackson.databind.ObjectMapper;
import exchange.ExchangeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.classes.Exchange;
import util.classes.ExchangeRate;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchange")
public class CurrencyExchangeServlet extends HttpServlet {

    ExchangeDAO exchangeDAO = new ExchangeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        String json;

        ExchangeRate exchangeRate = new ExchangeRate();
        Exchange exchange = null;

        String base = request.getParameter("from");
        String target = request.getParameter("to");
        String usd = "USD";
        double amount = 0;
        try {
            amount = Double.parseDouble(request.getParameter("amount"));
        } catch (NumberFormatException e) {
            System.out.println("error");
        }

        if (base == null || target == null || amount == 0) {
            json = objectMapper.writeValueAsString("Отсутствует поле ввода");
            response.setStatus(400);
            PrintWriter msg = response.getWriter();
            msg.print(json);
            msg.flush();
            return;
        }

        exchangeRate.setAmount(amount);

        try {
            if (base.compareToIgnoreCase(target) < 0) {
                exchange = exchangeDAO.getExchangeRate(base, target);
                if (exchange != null) {
                    exchange.reverseRate();
                }
            } else if (base.compareToIgnoreCase(target) > 0) {
                exchange = exchangeDAO.getExchangeRate(target, base);
                if (exchange != null) {
                    exchange.swap();
                }
            } else {
                System.out.println("in4");
                json = objectMapper.writeValueAsString("Вы ввели одинаковые валюты");
                response.setStatus(400);
                PrintWriter msg = response.getWriter();
                msg.print(json);
                msg.flush();
                return;
            }
        } catch (IOException e) {
            System.out.println("вышла ошибка");
        }
        if (exchange != null) {
            exchangeRate.setBaseCurrency(exchange.getBaseCurrency());
            exchangeRate.setTargetCurrency(exchange.getTargetCurrency());
            exchangeRate.setRate(exchange.getRate());
            exchangeRate.setConvertedAmount(exchangeRate.getAmount() * exchangeRate.getRate());
        }

        if (exchange == null) {
            Exchange usdBase = null;
            Exchange usdTarget = null;
            if (base.compareToIgnoreCase(usd) < 0) {
                usdBase = exchangeDAO.getExchangeRate(base, usd);
                usdBase.swap();
            } else if (base.compareToIgnoreCase(usd) > 0) {
                usdBase = exchangeDAO.getExchangeRate(usd, base);
                usdBase.reverseRate();
            }

            if (target.compareToIgnoreCase(usd) < 0) {
                usdTarget = exchangeDAO.getExchangeRate(target, usd);
                usdTarget.swap();
            } else if (target.compareToIgnoreCase(usd) > 0) {
                usdTarget = exchangeDAO.getExchangeRate(usd, target);
                usdTarget.reverseRate();
            }
            if (usdBase != null && usdTarget != null){
                exchangeRate.setBaseCurrency(usdBase.getTargetCurrency());
                exchangeRate.setTargetCurrency(usdTarget.getTargetCurrency());
                exchangeRate.setRate(usdTarget.getRate() / usdBase.getRate());
                exchangeRate.setConvertedAmount(exchangeRate.getAmount() * exchangeRate.getRate());
            } else {
                json = objectMapper.writeValueAsString("Валюта не найдена");
                response.setStatus(404);
                PrintWriter msg = response.getWriter();
                msg.print(json);
                msg.flush();
            }
        }
        json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(exchangeRate);
        PrintWriter out = response.getWriter();
        out.print(json);
        out.flush();
    }
}
