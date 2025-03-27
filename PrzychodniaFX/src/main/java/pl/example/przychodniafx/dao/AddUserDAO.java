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
            pstmt.setInt(8, user.getAccess_level() != null ? user.getAccess_level() : 1); // Default to basic access level (1)
            /*
            pstmt.setString(5, user.getPhone_number());
            pstmt.setString(6, user.getCity());
            pstmt.setString(7, user.getPostal_code());
            pstmt.setString(8, user.getStreet());
            pstmt.setString(9, user.getHouse_number());
            pstmt.setString(10, user.getApartment_number());

             */
            //pstmt.setString(5, user.getGender() != null ? user.getGender().toString() : null);
            //pstmt.setString(12, user.getEmail());

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
    //Szukanie Usera po peselu
    public User getUserByPesel(String pesel) throws SQLException {
        String sql = "SELECT * FROM Users WHERE pesel = ?";

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
        String sql = "SELECT * FROM Users";
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

    //Wypisywanie danych usera
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User(
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("pesel"),
                rs.getString("birth_date")
                //rs.getString("phone_number")
        );

        user.setUser_id(rs.getInt("user_id"));
        user.setLogin(rs.getString("login"));
        user.setPassword(rs.getString("password"));
        user.setAccess_level(rs.getInt("access_level"));
        
        // Pobieranie flagi is_forgotten
        user.setIs_forgotten(rs.getBoolean("is_forgotten"));
        
        /*
        user.setCity(rs.getString("city"));
        user.setPostal_code(rs.getString("postal_code"));
        user.setStreet(rs.getString("street"));
        user.setHouse_number(rs.getString("house_number"));
        user.setApartment_number(rs.getString("apartment_number"));
        user.setEmail(rs.getString("email"));
         */
        String genderStr = rs.getString("gender");
        if (genderStr != null && !genderStr.isEmpty()) {
            user.setGender(genderStr.charAt(0));
        }

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
}

