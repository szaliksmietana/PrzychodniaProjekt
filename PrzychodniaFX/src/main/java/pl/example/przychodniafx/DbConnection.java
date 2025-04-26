package pl.example.przychodniafx;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/przychodnia";
    private static final String USER = "root"; // Zmień na swoją nazwę użytkownika
    private static final String PASSWORD = "12345"; // Zmień na swoje hasło

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    // Deprecated method, kept for backward compatibility
    public static Connection connect() throws SQLException {
        return getConnection();
    }
    
    public static void initializePermissionsTables() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            // Load SQL from resource file
            InputStream is = DbConnection.class.getResourceAsStream("/pl/example/przychodniafx/sql/create_permissions_tables.sql");
            if (is == null) {
                System.err.println("Nie można znaleźć pliku SQL do inicjalizacji uprawnień");
                return;
            }
            
            String sql = new BufferedReader(new InputStreamReader(is))
                    .lines()
                    .collect(Collectors.joining("\n"));
            
            // Split by semicolons to execute multiple statements
            for (String query : sql.split(";")) {
                if (!query.trim().isEmpty()) {
                    statement.execute(query);
                }
            }
            
            System.out.println("Tabele uprawnień zostały zainicjalizowane");
            
        } catch (SQLException e) {
            System.err.println("Błąd podczas inicjalizacji tabel uprawnień: " + e.getMessage());
            e.printStackTrace();
        }
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
