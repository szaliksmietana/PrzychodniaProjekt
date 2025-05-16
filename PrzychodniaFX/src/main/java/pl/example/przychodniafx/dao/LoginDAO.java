package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.DbConnection;
import pl.example.przychodniafx.model.User;

import java.sql.*;

public class LoginDAO {

    public User authenticate(String login, String password) throws SQLException {
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
}
