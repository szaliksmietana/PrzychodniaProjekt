package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.DbConnection;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.model.PasswordUtils;
import pl.example.przychodniafx.email.TemporaryPasswordManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.*;

public class LoginDAO {

    public User authenticate(String login, String password) throws SQLException {
        // Najpierw sprawdź hasło tymczasowe
        if (TemporaryPasswordManager.validateTemporaryPassword(login, password)) {
            return getUserByLogin(login);
        }

        String sql = "SELECT * FROM Users WHERE login = ? AND password = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUser_id(rs.getInt("user_id"));
                    user.setLogin(rs.getString("login"));
                    user.setPassword(rs.getString("password"));
                    return user;
                }
            }
        }
        return null;
    }

    public User getUserByLogin(String login) throws SQLException {
        String query = "SELECT user_id, login, password, email, pesel, role FROM users WHERE login = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUser_id(rs.getInt("user_id"));
                user.setLogin(rs.getString("login"));
                user.setPassword(rs.getString("password"));
                user.setPesel(rs.getString("pesel"));
                return user;
            }
        }
        return null;
    }

}
