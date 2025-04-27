package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchUserDAO {

    public List<User> searchUsersByCombinedName(String searchText, boolean showForgotten) throws SQLException {
        if (searchText == null || searchText.trim().isEmpty()) {
            return getAllUsers(showForgotten);
        }

        if (searchText.contains(" ")) {
            String[] parts = searchText.split("\\s+", 2);
            String firstName = parts[0];
            String lastName = parts[1];
            return searchUsersByName(firstName, lastName, showForgotten);
        } else {
            List<User> foundByFirstName = searchUsersByName(searchText, "", showForgotten);
            List<User> foundByLastName = searchUsersByName("", searchText, showForgotten);

            for (User user : foundByLastName) {
                if (!foundByFirstName.contains(user)) {
                    foundByFirstName.add(user);
                }
            }

            return foundByFirstName;
        }
    }

    public List<User> searchUsersByName(String firstName, String lastName, boolean showForgotten) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT u.*, r.role_name " +
                        "FROM Users u " +
                        "LEFT JOIN user_roles ur ON u.user_id = ur.user_id " +
                        "LEFT JOIN roles r ON ur.role_id = r.role_id " +
                        "WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (showForgotten) {
            sqlBuilder.append(" AND u.is_forgotten = TRUE");
        } else {
            sqlBuilder.append(" AND (u.is_forgotten IS NULL OR u.is_forgotten = FALSE)");
        }

        if (firstName != null && !firstName.isEmpty()) {
            sqlBuilder.append(" AND u.first_name LIKE ?");
            params.add("%" + firstName + "%");
        }

        if (lastName != null && !lastName.isEmpty()) {
            sqlBuilder.append(" AND u.last_name LIKE ?");
            params.add("%" + lastName + "%");
        }

        sqlBuilder.append(" ORDER BY u.last_name, u.first_name");

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

    public User getUserById(int userId, boolean showForgotten) throws SQLException {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT u.*, r.role_name " +
                        "FROM Users u " +
                        "LEFT JOIN user_roles ur ON u.user_id = ur.user_id " +
                        "LEFT JOIN roles r ON ur.role_id = r.role_id " +
                        "WHERE u.user_id = ?"
        );

        if (showForgotten) {
            sqlBuilder.append(" AND u.is_forgotten = TRUE");
        } else {
            sqlBuilder.append(" AND (u.is_forgotten IS NULL OR u.is_forgotten = FALSE)");
        }

        String sql = sqlBuilder.toString();

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

    public List<User> getAllUsers(boolean showForgotten) throws SQLException {
        String sql =
                "SELECT u.*, r.role_name " +
                        "FROM Users u " +
                        "LEFT JOIN user_roles ur ON u.user_id = ur.user_id " +
                        "LEFT JOIN roles r ON ur.role_id = r.role_id " +
                        (showForgotten
                                ? "WHERE u.is_forgotten = TRUE"
                                : "WHERE u.is_forgotten IS NULL OR u.is_forgotten = FALSE") +
                        " ORDER BY u.last_name, u.first_name";

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

        String roleName = rs.getString("role_name");
        if (roleName != null) {
            user.setRoleName(roleName);
        }

        return user;
    }
}
