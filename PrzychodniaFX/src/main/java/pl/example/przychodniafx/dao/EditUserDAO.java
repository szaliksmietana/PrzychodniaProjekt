package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EditUserDAO {
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET first_name = ?, last_name = ?, pesel = ?, birth_date = ? " +
                "WHERE user_id = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirst_name());
            pstmt.setString(2, user.getLast_name());
            pstmt.setString(3, user.getPesel());
            pstmt.setString(4, user.getBirth_date());
            /*
            pstmt.setString(5, user.getPhone_number());
            pstmt.setString(6, user.getCity());
            pstmt.setString(7, user.getPostal_code());
            pstmt.setString(8, user.getStreet());
            pstmt.setString(9, user.getHouse_number());
            pstmt.setString(10, user.getApartment_number());
            pstmt.setString(12, user.getEmail());
             */
            pstmt.setString(11, user.getGender() != null ? user.getGender().toString() : null);
            pstmt.setInt(13, user.getUser_id());

            pstmt.executeUpdate();
        }
    }
}
