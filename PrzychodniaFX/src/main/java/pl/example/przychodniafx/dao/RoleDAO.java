package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.DbConnection;
import pl.example.przychodniafx.model.Roles;
import pl.example.przychodniafx.model.UserPermission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {

    // Pobiera wszystkie role dostępne w systemie
    public List<Roles> getAllRoles() throws SQLException {
        String sql = "SELECT * FROM roles";
        List<Roles> roles = new ArrayList<>();

        try (Connection conn = DbConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Roles role = new Roles(rs.getString("role_name"));
                role.setRole_id(rs.getInt("role_id"));
                roles.add(role);
            }
        }

        return roles;
    }

    // Przypisuje rolę do użytkownika
    public void assignRoleToUser(int userId, int roleId) throws SQLException {
        String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, roleId);
            pstmt.executeUpdate();
        }
    }

    // Pobiera uprawnienia dla określonej roli
    public List<UserPermission> getPermissionsForRole(int roleId) throws SQLException {
        String sql = "SELECT p.permission_id, p.permission_name, r.role_id, r.role_name " +
                "FROM permissions p " +
                "JOIN role_permissions rp ON p.permission_id = rp.permission_id " +
                "JOIN roles r ON rp.role_id = r.role_id " +
                "WHERE r.role_id = ?";

        List<UserPermission> permissions = new ArrayList<>();

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserPermission permission = new UserPermission(
                            rs.getInt("permission_id"),
                            rs.getString("permission_name"),
                            rs.getInt("role_id"),
                            rs.getString("role_name")
                    );
                    permissions.add(permission);
                }
            }
        }

        return permissions;
    }

    // Pobiera wszystkie uprawnienia użytkownika
    public List<UserPermission> getUserPermissions(int userId) throws SQLException {
        String sql = "SELECT p.permission_id, p.permission_name, r.role_id, r.role_name " +
                "FROM permissions p " +
                "JOIN role_permissions rp ON p.permission_id = rp.permission_id " +
                "JOIN roles r ON rp.role_id = r.role_id " +
                "JOIN user_roles ur ON r.role_id = ur.role_id " +
                "WHERE ur.user_id = ?";

        List<UserPermission> permissions = new ArrayList<>();

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    UserPermission permission = new UserPermission(
                            rs.getInt("permission_id"),
                            rs.getString("permission_name"),
                            rs.getInt("role_id"),
                            rs.getString("role_name")
                    );
                    permissions.add(permission);
                }
            }
        }

        return permissions;
    }
}