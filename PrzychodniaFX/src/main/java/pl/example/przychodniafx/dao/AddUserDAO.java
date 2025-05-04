package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO odpowiedzialne za operacje dodawania użytkowników.
 */
public class AddUserDAO extends BaseDAO {

    /**
     * Sprawdza, czy użytkownik o podanym PESEL już istnieje.
     * 
     * @param pesel PESEL do sprawdzenia
     * @return true jeśli użytkownik istnieje, false w przeciwnym przypadku
     * @throws SQLException w przypadku błędu bazy danych
     */
    public boolean isPeselExists(String pesel) throws SQLException {
        if (pesel == null || pesel.trim().isEmpty()) {
            throw new IllegalArgumentException("PESEL cannot be null or empty");
        }
        
        String query = "SELECT COUNT(*) FROM users WHERE pesel = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, pesel);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Pobiera wszystkich użytkowników z bazy danych.
     * 
     * @return lista wszystkich użytkowników
     * @throws SQLException w przypadku błędu bazy danych
     */
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT u.*, r.role_name " +
                "FROM users u " +
                "LEFT JOIN userroles ur ON u.user_id = ur.user_id " +
                "LEFT JOIN roles r ON ur.role_id = r.role_id";

        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }

        return users;
    }

    protected User extractUserFromResultSet(ResultSet rs) throws SQLException {
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

    /**
     * Dodaje nowego użytkownika i zwraca jego ID.
     * 
     * @param user obiekt użytkownika do dodania
     * @return ID dodanego użytkownika
     * @throws SQLException w przypadku błędu bazy danych
     * @throws IllegalArgumentException jeśli dane użytkownika są niepoprawne
     */
    public int addUserAndReturnId(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (user.getFirst_name() == null || user.getFirst_name().trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be null or empty");
        }
        if (user.getLast_name() == null || user.getLast_name().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be null or empty");
        }
        if (user.getPesel() == null || user.getPesel().trim().isEmpty()) {
            throw new IllegalArgumentException("PESEL cannot be null or empty");
        }
        
        String sql = "INSERT INTO users (first_name, last_name, pesel, birth_date, gender, login, password, access_level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getFirst_name());
            pstmt.setString(2, user.getLast_name());
            pstmt.setString(3, user.getPesel());
            pstmt.setString(4, user.getBirth_date());
            pstmt.setString(5, user.getGender() != null ? user.getGender().toString() : null);
            pstmt.setString(6, user.getLogin() != null ? user.getLogin() : user.getPesel());
            pstmt.setString(7, user.getPassword() != null ? user.getPassword() : user.getPesel());
            pstmt.setInt(8, user.getAccess_level() != null ? user.getAccess_level() : 1);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        throw new SQLException("Nie udało się dodać użytkownika i pobrać ID");
    }
}
