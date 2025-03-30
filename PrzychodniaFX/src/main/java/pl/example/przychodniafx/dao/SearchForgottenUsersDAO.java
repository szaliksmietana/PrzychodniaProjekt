package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchForgottenUsersDAO {
    public User getForgottenUserByNameAndSurname(String firstName, String lastName) throws SQLException {
        String sql = "SELECT * FROM Users WHERE first_name = ? AND last_name = ? AND is_forgotten = TRUE";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }

        return null;
    }

    public List<User> searchForgottenUsersByName(String firstName, String lastName) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM Users WHERE is_forgotten = TRUE");
        List<Object> params = new ArrayList<>();

        if (firstName != null && !firstName.isEmpty()) {
            sqlBuilder.append(" AND first_name LIKE ?");
            params.add("%" + firstName + "%");
        }

        if (lastName != null && !lastName.isEmpty()) {
            sqlBuilder.append(" AND last_name LIKE ?");
            params.add("%" + lastName + "%");
        }

        String sql = sqlBuilder.toString();
        List<User> users = new ArrayList<>();

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        }

        return users;
    }

    public User getForgottenUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM Users WHERE user_id = ? AND is_forgotten = TRUE";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        }

        return null;
    }

    public List<User> getAllForgottenUsersByNameAndSurname(String firstName, String lastName) throws SQLException {
        String sql = "SELECT * FROM Users WHERE first_name LIKE ? AND last_name LIKE ? AND is_forgotten = TRUE";
        List<User> users = new ArrayList<>();

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + firstName + "%");
            pstmt.setString(2, "%" + lastName + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        }

        return users;
    }

    public List<User> getAllForgottenUsers() throws SQLException {
        String sql = "SELECT * FROM Users WHERE is_forgotten = TRUE";
        List<User> users = new ArrayList<>();

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

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

        user.setUser_id(rs.getInt("user_id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setAccess_level(rs.getInt("access_level"));
        user.setIs_forgotten(rs.getBoolean("is_forgotten"));

        String genderStr = rs.getString("gender");
        if (genderStr != null && !genderStr.isEmpty()) {
            user.setGender(genderStr.charAt(0));
        }

        return user;
    }

}
