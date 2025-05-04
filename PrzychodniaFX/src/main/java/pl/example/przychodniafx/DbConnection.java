package pl.example.przychodniafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Klasa odpowiedzialna za połączenie z bazą danych MySQL Workbench.
 */
public class DbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/przychodnia";
    private static final String USER = "root"; // Użytkownik MySQL
    private static final String PASSWORD = "12345"; // Hasło MySQL

    /**
     * Zwraca połączenie z bazą danych.
     * @return Obiekt połączenia z bazą danych
     * @throws SQLException w przypadku błędu połączenia
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    /**
     * Metoda alternatywna dla zachowania kompatybilności wstecznej.
     * @return Obiekt połączenia z bazą danych
     * @throws SQLException w przypadku błędu połączenia
     */
    public static Connection connect() throws SQLException {
        return getConnection();
    }
    
    /**
     * Metoda testowa do sprawdzenia połączenia z bazą danych.
     */
    public static void main(String[] args) {
        try (Connection conn = connect()) {
            System.out.println("Połączenie z bazą danych nawiązane pomyślnie!");
        } catch (SQLException e) {
            System.err.println("Błąd połączenia z bazą danych: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
