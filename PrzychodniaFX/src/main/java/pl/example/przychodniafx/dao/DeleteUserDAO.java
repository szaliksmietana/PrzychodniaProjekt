package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteUserDAO {
    
    public boolean deleteUser(User user) throws SQLException {
        return deleteUserById(user.getUser_id());
    }
    
    public boolean deleteUserById(int userId) throws SQLException {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        
        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    public boolean deleteUserByPesel(String pesel) throws SQLException {
        String sql = "DELETE FROM Users WHERE pesel = ?";
        
        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, pesel);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        }
    }
}
