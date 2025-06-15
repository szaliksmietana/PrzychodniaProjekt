package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.DbConnection;
import pl.example.przychodniafx.model.Permissions;
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

    // Pobiera wszystkie uprawnienia dostępne w systemie
    public List<Permissions> getAllPermissions() throws SQLException {
        String sql = "SELECT * FROM permissions";
        List<Permissions> permissions = new ArrayList<>();

        try (Connection conn = DbConnection.connect();
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

    // Sprawdza czy użytkownik ma przypisaną określoną rolę
    public boolean userHasRole(int userId, int roleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user_roles WHERE user_id = ? AND role_id = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, roleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    // Pobiera lub tworzy niestandardową rolę dla użytkownika
    public int getOrCreateCustomRole(int userId, String roleName) throws SQLException {
        // Najpierw sprawdź czy rola już istnieje
        String checkSql = "SELECT role_id FROM roles WHERE role_name = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, roleName);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("role_id");
                }
            }

            // Jeśli rola nie istnieje, utwórz ją
            String insertSql = "INSERT INTO roles (role_name) VALUES (?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, roleName);
                insertStmt.executeUpdate();

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Nie udało się utworzyć niestandardowej roli, nie uzyskano ID.");
                    }
                }
            }
        }
    }

    // Przypisuje uprawnienie do roli
    public void assignPermissionToRole(int roleId, int permissionId) throws SQLException {
        // Najpierw sprawdź czy uprawnienie już jest przypisane do roli
        String checkSql = "SELECT COUNT(*) FROM role_permissions WHERE role_id = ? AND permission_id = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, roleId);
            checkStmt.setInt(2, permissionId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    // Uprawnienie już istnieje dla tej roli
                    return;
                }
            }

            // Jeśli uprawnienie nie jest przypisane, dodaj je
            String insertSql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, roleId);
                insertStmt.setInt(2, permissionId);
                insertStmt.executeUpdate();
            }
        }
    }

    // Usuwa uprawnienie z roli niestandardowej
    public void removePermissionFromCustomRole(int roleId, int permissionId) throws SQLException {
        String sql = "DELETE FROM role_permissions WHERE role_id = ? AND permission_id = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roleId);
            pstmt.setInt(2, permissionId);
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


}