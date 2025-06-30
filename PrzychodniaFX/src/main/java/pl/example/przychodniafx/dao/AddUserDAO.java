package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import pl.example.przychodniafx.model.PasswordUtils;


public class AddUserDAO {

    public boolean isPeselExists(String pesel) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE pesel = ?";
        try (Connection conn = DbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, pesel);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public User getUserByPesel(String pesel) throws SQLException {
        String sql = "SELECT u.*, r.role_name " +
                "FROM Users u " +
                "LEFT JOIN user_roles ur ON u.user_id = ur.user_id " +
                "LEFT JOIN roles r ON ur.role_id = r.role_id " +
                "WHERE u.pesel = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pesel);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }

        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT u.*, r.role_name " +
                "FROM Users u " +
                "LEFT JOIN user_roles ur ON u.user_id = ur.user_id " +
                "LEFT JOIN roles r ON ur.role_id = r.role_id";

        List<User> users = new ArrayList<>();

        try (Connection conn = DbConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }

        return users;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("pesel"),
                rs.getString("birth_date")
        );

        user.setId(rs.getInt("user_id"));

        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setAccessLevel(rs.getInt("access_level"));
        user.setIsForgotten(rs.getBoolean("is_forgotten"));

        String genderStr = rs.getString("gender");
        if (genderStr != null && !genderStr.isEmpty()) {
            user.setGender(genderStr.charAt(0));
        }

        user.setRoleName(rs.getString("role_name"));
        return user;
    }

    public int addUserAndReturnId(User user) throws SQLException {
        String sql = "INSERT INTO Users (first_name, last_name, pesel, birth_date, gender, login, password, access_level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        // Haszowanie hasła przed zapisaniem
        String hashedPassword = PasswordUtils.hashPassword(user.getPassword());

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPesel());
            pstmt.setString(4, user.getBirthDate());
            pstmt.setString(5, user.getGender().toString());
            pstmt.setString(6, user.getLogin() != null ? user.getLogin() : user.getPesel());
            pstmt.setString(7, hashedPassword); // Zapisz zahaszowane hasło
            pstmt.setInt(8, user.getAccessLevel() != null ? user.getAccessLevel() : 1);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }

        throw new SQLException("Nie udało się dodać użytkownika i pobrać ID");
    }


    public void updateUserPassword(int userId, String newPassword) throws SQLException {


        String sql = "UPDATE Users SET password = ? WHERE user_id = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setInt(2, userId);

            pstmt.executeUpdate();
        }
    }
}