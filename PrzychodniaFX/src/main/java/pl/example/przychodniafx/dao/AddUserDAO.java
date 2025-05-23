package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddUserDAO {

    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO Users (first_name, last_name, pesel, birth_date, gender, login, password, access_level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFirst_name());
            pstmt.setString(2, user.getLast_name());
            pstmt.setString(3, user.getPesel());
            pstmt.setString(4, user.getBirth_date());
            pstmt.setString(5, user.getGender().toString());
            pstmt.setString(6, user.getLogin() != null ? user.getLogin() : user.getPesel());
            pstmt.setString(7, user.getPassword() != null ? user.getPassword() : user.getPesel());
            pstmt.setInt(8, user.getAccess_level() != null ? user.getAccess_level() : 1);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        user.setUser_id(rs.getInt(1));
                    }
                }
            }
        }
    }

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

        user.setUser_id(rs.getInt("user_id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setAccess_level(rs.getInt("access_level"));
        user.setIs_forgotten(rs.getBoolean("is_forgotten"));

        String genderStr = rs.getString("gender");
        if (genderStr != null && !genderStr.isEmpty()) {
            user.setGender(genderStr.charAt(0));
        }

        user.setRoleName(rs.getString("role_name")); // <-- dodano roleName
        return user;
    }

    public void updateUserForgottenStatus(User user) throws SQLException {
        String sql = "UPDATE Users SET is_forgotten = ? WHERE user_id = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, user.getIs_forgotten());
            pstmt.setInt(2, user.getUser_id());

            pstmt.executeUpdate();
        }
    }
    public int addUserAndReturnId(User user) throws SQLException {
        String sql = "INSERT INTO Users (first_name, last_name, pesel, birth_date, gender, login, password, access_level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFirst_name());
            pstmt.setString(2, user.getLast_name());
            pstmt.setString(3, user.getPesel());
            pstmt.setString(4, user.getBirth_date());
            pstmt.setString(5, user.getGender().toString());
            pstmt.setString(6, user.getLogin() != null ? user.getLogin() : user.getPesel());
            pstmt.setString(7, user.getPassword() != null ? user.getPassword() : user.getPesel());
            pstmt.setInt(8, user.getAccess_level() != null ? user.getAccess_level() : 1);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // <- Zwracamy user_id
                    }
                }
            }
        }
        throw new SQLException("Nie udało się dodać użytkownika i pobrać ID");
    }

}
