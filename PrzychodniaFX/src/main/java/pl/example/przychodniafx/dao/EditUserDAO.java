package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO odpowiedzialne za operacje edycji użytkowników.
 */
public class EditUserDAO extends BaseDAO {
    
    /**
     * Aktualizuje dane użytkownika w bazie danych.
     * 
     * @param user obiekt użytkownika z zaktualizowanymi danymi
     * @throws SQLException w przypadku błędu bazy danych
     * @throws IllegalArgumentException jeśli dane użytkownika są nieprawidłowe
     */
    public void updateUser(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getUser_id() == null || user.getUser_id() <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        if (user.getFirst_name() == null || user.getFirst_name().trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (user.getLast_name() == null || user.getLast_name().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        
        String sql = "UPDATE users SET first_name = ?, last_name = ?, pesel = ?, birth_date = ?, gender = ? " +
                "WHERE user_id = ?";

        try (Connection conn = getConnection();
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
            pstmt.setString(5, user.getGender() != null ? user.getGender().toString() : null);
            pstmt.setInt(6, user.getUser_id());

            pstmt.executeUpdate();
        }
    }
}
