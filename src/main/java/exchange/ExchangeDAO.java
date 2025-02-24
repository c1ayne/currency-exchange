package exchange;

import util.classes.Currency;
import util.classes.Exchange;

import java.sql.*;
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

        ArrayList<Exchange> exchanges = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            String query = "SELECT " +
                    "er.ID, " +
                    "bc.ID AS BaseCurrencyID, " +
                    "bc.fullName AS BaseCurrencyName, " +
                    "bc.code AS BaseCurrencyCode, " +
                    "bc.sign AS BaseCurrencySign, " +
                    "tc.ID AS TargetCurrencyID, " +
                    "tc.fullName AS TargetCurrencyName, " +
                    "tc.code AS TargetCurrencyCode, " +
                    "tc.sign AS TargetCurrencySign, " +
                    "er.rate " +
                    "FROM exchangeRates er " +
                    "JOIN Currencies bc ON er.BaseCurrencyId = bc.ID " +
                    "JOIN Currencies tc ON er.TargetCurrencyId = tc.ID";

            Connection connection = DriverManager.getConnection(url, name, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                Currency baseCurrency = new Currency(
                        resultSet.getInt("BaseCurrencyID"),
                        resultSet.getString("BaseCurrencyCode"),
                        resultSet.getString("BaseCurrencyName"),
                        resultSet.getString("BaseCurrencySign")
                );
                Currency targetCurrency = new Currency(
                        resultSet.getInt("TargetCurrencyID"),
                        resultSet.getString("TargetCurrencyCode"),
                        resultSet.getString("TargetCurrencyName"),
                        resultSet.getString("TargetCurrencySign")
                );
                exchanges.add(new Exchange(
                        resultSet.getInt("ID"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getDouble("rate")
                ));

                return exchanges;
            }

            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return exchanges;
    }

    public Exchange getExchangeRate(String base,String target) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            String query = "SELECT " +
                        "er.ID, " +
                        "bc.ID AS BaseCurrencyID, " +
                        "bc.fullName AS BaseCurrencyName, " +
                        "bc.code AS BaseCurrencyCode, " +
                        "bc.sign AS BaseCurrencySign, " +
                        "tc.ID AS TargetCurrencyID, " +
                        "tc.fullName AS TargetCurrencyName, " +
                        "tc.code AS TargetCurrencyCode, " +
                        "tc.sign AS TargetCurrencySign, " +
                        "er.rate " +
                    "FROM exchangeRates er " +
                    "JOIN Currencies bc ON er.BaseCurrencyId = bc.ID " +
                    "JOIN Currencies tc ON er.TargetCurrencyId = tc.ID " +
                    "WHERE bc.code = ? AND tc.code = ?";

            Connection connection = DriverManager.getConnection(url, name, password);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, base);
            preparedStatement.setString(2, target);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Currency baseCurrency = new Currency(
                        resultSet.getInt("BaseCurrencyID"),
                        resultSet.getString("BaseCurrencyCode"),
                        resultSet.getString("BaseCurrencyName"),
                        resultSet.getString("BaseCurrencySign")
                );
                Currency targetCurrency = new Currency(
                        resultSet.getInt("TargetCurrencyID"),
                        resultSet.getString("TargetCurrencyCode"),
                        resultSet.getString("TargetCurrencyName"),
                        resultSet.getString("TargetCurrencySign")
                );
                Exchange exchange = new Exchange(
                        resultSet.getInt("ID"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getDouble("rate")
                );

                connection.close();

                return exchange;
            } else connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Exchange setExchangeRate(String baseCode, String targetCode, double rate) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            String query = "WITH inserted AS (" +
                        "INSERT INTO exchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) " +
                        "VALUES ((SELECT ID FROM Currencies WHERE code = ?), " +
                        "(SELECT ID FROM Currencies WHERE code = ?), ?) RETURNING *" +
                        ") " +
                    "SELECT " +
                        "i.ID," +
                        "bc.ID AS BaseCurrencyID," +
                        "bc.FullName AS BaseCurrencyName," +
                        "bc.Code AS BaseCurrencyCode," +
                        "bc.Sign AS BaseCurrencySign," +
                        "tc.ID AS TargetCurrencyID," +
                        "tc.FullName AS TargetCurrencyName," +
                        "tc.Code AS TargetCurrencyCode," +
                        "tc.Sign AS TargetCurrencySign," +
                        "i.Rate " +
                    "FROM inserted i " +
                    "JOIN Currencies bc ON i.BaseCurrencyId = bc.ID " +
                    "JOIN Currencies tc ON i.TargetCurrencyId = tc.ID";
            Connection connection = DriverManager.getConnection(url, name, password);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);
            preparedStatement.setDouble(3, rate);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Currency baseCurrency = new Currency(
                        resultSet.getInt("BaseCurrencyID"),
                        resultSet.getString("BaseCurrencyCode"),
                        resultSet.getString("BaseCurrencyName"),
                        resultSet.getString("BaseCurrencySign")
                );
                Currency targetCurrency = new Currency(
                        resultSet.getInt("TargetCurrencyID"),
                        resultSet.getString("TargetCurrencyCode"),
                        resultSet.getString("TargetCurrencyName"),
                        resultSet.getString("TargetCurrencySign")
                );
                Exchange exchange = new Exchange(
                        resultSet.getInt("ID"),
                        baseCurrency,
                        targetCurrency,
                        resultSet.getDouble("rate")
                );

                connection.close();

                return exchange;
            } else connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Exchange updateExchangeRate() {
        return null;
    }
}
