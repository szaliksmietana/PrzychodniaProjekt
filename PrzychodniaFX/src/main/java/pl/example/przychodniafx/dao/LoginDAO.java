package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.DbConnection;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.email.TemporaryPasswordManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    public User authenticate(String login, String password) throws SQLException {
        // Jeśli pasuje tymczasowe hasło
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
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    public User getUserByLogin(String login) throws SQLException {
        String query = "SELECT * FROM Users WHERE login = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }
        return null;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setPesel(rs.getString("pesel"));
        user.setBirthDate(rs.getString("birth_date"));

        String genderStr = rs.getString("gender");
        if (genderStr != null && !genderStr.isEmpty()) {
            user.setGender(genderStr.charAt(0));
        }

        user.setAccessLevel(rs.getInt("access_level"));
        user.setIsForgotten(rs.getBoolean("is_forgotten"));

        return user;
    }
}
