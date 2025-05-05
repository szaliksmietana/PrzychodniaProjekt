package pl.example.przychodniafx.dao;

import pl.example.przychodniafx.DbConnection;
import pl.example.przychodniafx.model.Permissions;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.model.UserPermission;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ListPermissionsDAO {

    public List<Permissions> getAllPermissions() throws SQLException {
        List<Permissions> permissions = new ArrayList<>();
        String query = "SELECT permission_id, permission_name FROM permissions";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("permission_id");
                String name = resultSet.getString("permission_name");
                permissions.add(new Permissions(id, name));
            }
        }

        return permissions;
    }

    public List<User> getUsersByPermission(int permissionId) throws SQLException {
        List<User> users = new ArrayList<>();

        String query = """
            SELECT u.user_id, u.first_name, u.last_name, u.pesel, u.birth_date, u.gender, u.is_forgotten, r.role_name
            FROM Users u
            JOIN user_roles ur ON u.user_id = ur.user_id
            JOIN roles r ON ur.role_id = r.role_id
            JOIN role_permissions rp ON r.role_id = rp.role_id
            WHERE rp.permission_id = ? AND (u.is_forgotten IS NULL OR u.is_forgotten = 0)
            ORDER BY u.last_name, u.first_name
        """;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, permissionId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setUser_id(resultSet.getInt("user_id"));
                    user.setFirst_name(resultSet.getString("first_name"));
                    user.setLast_name(resultSet.getString("last_name"));
                    user.setPesel(resultSet.getString("pesel"));
                    user.setBirth_date(resultSet.getString("birth_date"));
                    user.setGender(resultSet.getString("gender").charAt(0));
                    Boolean isForgotten = resultSet.getObject("is_forgotten") != null ?
                            resultSet.getBoolean("is_forgotten") : false;
                    user.setIs_forgotten(isForgotten);

                    user.setRoleName(resultSet.getString("role_name")); // <--- pobieranie roli

                    users.add(user);
                }
            }
        }

        return users;
    }

    public List<UserPermission> getUserPermissions(int userId) throws SQLException {
        List<UserPermission> userPermissions = new ArrayList<>();

        String query = """
            SELECT p.permission_id, p.permission_name, r.role_id, r.role_name
            FROM permissions p
            JOIN role_permissions rp ON p.permission_id = rp.permission_id
            JOIN roles r ON rp.role_id = r.role_id
            JOIN user_roles ur ON r.role_id = ur.role_id
            WHERE ur.user_id = ?
            ORDER BY p.permission_name
        """;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    UserPermission permission = new UserPermission();
                    permission.setPermissionId(resultSet.getInt("permission_id"));
                    permission.setPermissionName(resultSet.getString("permission_name"));
                    permission.setRoleId(resultSet.getInt("role_id"));
                    permission.setRoleName(resultSet.getString("role_name"));

                    userPermissions.add(permission);
                }
            }
        }

        return userPermissions;
    }
}
