package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ForgetUserDAO {
    
    public void forgetUser(User user) throws SQLException {
        String sql = "UPDATE Users SET is_forgotten = true WHERE user_id = ?";
        
        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, user.getUser_id());
            pstmt.executeUpdate();
        }
    }
    
    public void forgetUserById(int userId) throws SQLException {
        String sql = "UPDATE Users SET is_forgotten = true WHERE user_id = ?";
        
        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }
} 