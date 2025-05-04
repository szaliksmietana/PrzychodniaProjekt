package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.model.Permissions;
import pl.example.przychodniafx.model.Roles;
import pl.example.przychodniafx.model.UserPermission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO odpowiedzialne za operacje na rolach i uprawnieniach.
 */
public class RoleDAO extends BaseDAO {

    /**
     * Pobiera wszystkie role dostępne w systemie.
     * 
     * @return lista ról
     * @throws SQLException w przypadku błędu bazy danych
     */
    public List<Roles> getAllRoles() throws SQLException {
        String sql = "SELECT * FROM roles";
        List<Roles> roles = new ArrayList<>();

        try (Connection conn = getConnection();
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

    /**
     * Pobiera wszystkie uprawnienia dostępne w systemie.
     * 
     * @return lista uprawnień
     * @throws SQLException w przypadku błędu bazy danych
     */
    public List<Permissions> getAllPermissions() throws SQLException {
        String sql = "SELECT * FROM permissions";
        List<Permissions> permissions = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Permissions permission = new Permissions(
                        rs.getInt("permission_id"),
                        rs.getString("permission_name")
                );
                permissions.add(permission);
            }
        }

        return permissions;
    }

    /**
     * Przypisuje rolę do użytkownika.
     * 
     * @param userId ID użytkownika
     * @param roleId ID roli
     * @throws SQLException w przypadku błędu bazy danych
     * @throws IllegalArgumentException jeśli ID są nieprawidłowe
     */
    public void assignRoleToUser(int userId, int roleId) throws SQLException {
        validateId(userId, "User");
        validateId(roleId, "Role");
        
        String sql = "INSERT INTO userroles (user_id, role_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, roleId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Przypisuje bezpośrednio uprawnienia do użytkownika.
     * Zakłada, że tabela user_permissions istnieje w bazie danych.
     * 
     * @param userId ID użytkownika
     * @param permissionIds lista ID uprawnień
     * @throws SQLException w przypadku błędu bazy danych
     * @throws IllegalArgumentException jeśli parametry są nieprawidłowe
     */
    public void assignDirectPermissionsToUser(int userId, List<Integer> permissionIds) throws SQLException {
        validateId(userId, "User");
        if (permissionIds == null || permissionIds.isEmpty()) {
            throw new IllegalArgumentException("Permission IDs cannot be null or empty");
        }
        
        String sql = "INSERT INTO user_permissions (user_id, permission_id) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Integer permissionId : permissionIds) {
                validateId(permissionId, "Permission");
                pstmt.setInt(1, userId);
                pstmt.setInt(2, permissionId);
                pstmt.executeUpdate();
            }
        }
    }

    /**
     * Pobiera uprawnienia dla określonej roli.
     * 
     * @param roleId ID roli
     * @return lista uprawnień przypisanych do roli
     * @throws SQLException w przypadku błędu bazy danych
     * @throws IllegalArgumentException jeśli roleId jest nieprawidłowy
     */
    public List<UserPermission> getPermissionsForRole(int roleId) throws SQLException {
        validateId(roleId, "Role");
        
        String sql = "SELECT p.permission_id, p.permission_name, r.role_id, r.role_name " +
                "FROM permissions p " +
                "JOIN rolepermissions rp ON p.permission_id = rp.permission_id " +
                "JOIN roles r ON rp.role_id = r.role_id " +
                "WHERE r.role_id = ?";

        List<UserPermission> permissions = new ArrayList<>();

        try (Connection conn = getConnection();
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
}