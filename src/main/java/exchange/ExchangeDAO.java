package exchange;

import util.classes.Exchange;

import java.util.ArrayList;

public class ExchangeDAO {
    String url;
    String name;
    String password;

    public ExchangeDAO() {
        this.url = System.getenv("DB_URL");
        this.name = System.getenv("DB_LOGIN");
        this.password = System.getenv("DB_PASSWORD");

        if (url == null || name == null || password == null)
            throw new RuntimeException("Не заданы переменные окружения для подключения к БД");
    }

    public ArrayList<Exchange> getAllExchangeRates() {
        return null;
    }

    public Exchange getExchangeRate() {
        return null;
    }

    public Exchange setExchangeRate() {
        return null;
    }

    public Exchange updateExchangeRate() {
        return null;
    }
}
