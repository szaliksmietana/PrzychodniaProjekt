package pl.example.przychodniafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/przychodnia";
    private static final String USER = "root"; // Zmień na swoją nazwę użytkownika
    private static final String PASSWORD = "qwerty"; // Zmień na swoje hasło

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    /*
    public static void main(String[] args) {
        try (Connection conn = connect()) {
            System.out.println("BAZA DZIAŁA");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     */
}
