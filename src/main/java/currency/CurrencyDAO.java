package currency;

import util.classes.Currency;

import java.sql.*;
import java.util.ArrayList;

public class CurrencyDAO {

    String url = "jdbc:postgresql://localhost:5432/currency-exchange"; //DB url
    String name = "postgres"; //DB login
    String password = "root"; //DB password


    public ArrayList<Currency> getAllCurrencies() {

        ArrayList<Currency> currencies = new ArrayList<>();

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            Connection connection = DriverManager.getConnection(url, name, password);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM currencies");


            while (resultSet.next()) {
                currencies.add(new Currency(
                        resultSet.getInt("ID"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign")
                ));
            }
            connection.close();

            return currencies;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Currency getCurrency(String code) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            String query = "SELECT * FROM currencies WHERE code = ?";
            Connection connection = DriverManager.getConnection(url, name, password);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Currency currency = new Currency(
                        resultSet.getInt("ID"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign")
                );

                connection.close();

                return currency;
            }

            else connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public Currency setCurrency(String code, String fullName, String sign) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        try {
            String query = "INSERT INTO currencies (code, fullname, sign) VALUES (?, ?, ?) RETURNING *";
            Connection connection = DriverManager.getConnection(url, name, password);

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, code);
            preparedStatement.setString(2, fullName);
            preparedStatement.setString(3, sign);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Currency currency = new Currency(
                        resultSet.getInt("ID"),
                        resultSet.getString("code"),
                        resultSet.getString("fullname"),
                        resultSet.getString("sign")
                );

                connection.close();

                return currency;
            } else connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
