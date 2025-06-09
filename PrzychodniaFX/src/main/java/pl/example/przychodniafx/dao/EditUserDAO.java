package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.*;

public class EditUserDAO {
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE Users SET first_name = ?, last_name = ?, pesel = ?, birth_date = ?, gender = ? " +
                "WHERE user_id = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPesel());
            pstmt.setString(4, user.getBirthDate());
            pstmt.setString(5, user.getGender() != null ? user.getGender().toString() : null);
            pstmt.setInt(6, user.getId());




            pstmt.executeUpdate();
        }
    }
}
