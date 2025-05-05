package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ForgetUserDAO {
    
    /**
     * Mark a user as forgotten which will trigger the database trigger to anonymize user data
     * and insert into forgottenUsers table
     */
    public void forgetUser(User user) throws SQLException {
        String sql = "UPDATE Users SET is_forgotten = true WHERE user_id = ?";
        
        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, user.getUser_id());
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Mark a user as forgotten by ID which will trigger the database trigger
     */
    public void forgetUserById(int userId) throws SQLException {
        String sql = "UPDATE Users SET is_forgotten = true WHERE user_id = ?";
        
        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }
    
    /**
     * Check if a user is in the forgotten users table
     */
    public boolean isUserForgotten(int userId) throws SQLException {
        String sql = "SELECT * FROM forgottenUsers WHERE user_id = ?";
        
        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns true if user is in forgottenUsers table
            }
        }
    }
    
    /**
     * Get list of all forgotten user IDs
     */
    public List<Integer> getAllForgottenUserIds() throws SQLException {
        String sql = "SELECT user_id FROM forgottenUsers";
        List<Integer> forgottenUserIds = new ArrayList<>();
        
        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                forgottenUserIds.add(rs.getInt("user_id"));
            }
        }
        
        return forgottenUserIds;
    }
} 