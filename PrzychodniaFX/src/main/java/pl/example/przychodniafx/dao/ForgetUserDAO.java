package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO odpowiedzialne za operacje na "zapomnianych" użytkownikach.
 */
public class ForgetUserDAO extends BaseDAO {
    
    /**
     * Oznacza użytkownika jako "zapomnianego", co spowoduje uruchomienie wyzwalacza
     * do anonimizacji danych użytkownika i dodania go do tabeli forgotten_users.
     * 
     * @param user użytkownik do "zapomnienia"
     * @throws SQLException w przypadku błędu bazy danych
     * @throws IllegalArgumentException jeśli użytkownik jest nieprawidłowy
     */
    public void forgetUser(User user) throws SQLException {
        if (user == null || user.getUser_id() == null) {
            throw new IllegalArgumentException("User and user ID cannot be null");
        }
        forgetUserById(user.getUser_id());
    }
    
    /**
     * Oznacza użytkownika jako "zapomnianego" na podstawie ID.
     * 
     * @param userId ID użytkownika do "zapomnienia"
     * @throws SQLException w przypadku błędu bazy danych
     * @throws IllegalArgumentException jeśli userId jest nieprawidłowy
     */
    public void forgetUserById(int userId) throws SQLException {
        validateId(userId, "User");
        
        String sql = "UPDATE users SET is_forgotten = true WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Sprawdza, czy użytkownik jest w tabeli "zapomnianych" użytkowników.
     * 
     * @param userId ID użytkownika do sprawdzenia
     * @return true jeśli użytkownik jest "zapomniany", false w przeciwnym przypadku
     * @throws SQLException w przypadku błędu bazy danych
     * @throws IllegalArgumentException jeśli userId jest nieprawidłowy
     */
    public boolean isUserForgotten(int userId) throws SQLException {
        validateId(userId, "User");
        
        String sql = "SELECT * FROM forgottenusers WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    /**
     * Pobiera listę ID wszystkich "zapomnianych" użytkowników.
     * 
     * @return lista ID "zapomnianych" użytkowników
     * @throws SQLException w przypadku błędu bazy danych
     */
    public List<Integer> getAllForgottenUserIds() throws SQLException {
        String sql = "SELECT user_id FROM forgottenusers";
        List<Integer> forgottenUserIds = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                forgottenUserIds.add(rs.getInt("user_id"));
            }
        }
        
        return forgottenUserIds;
    }
} 