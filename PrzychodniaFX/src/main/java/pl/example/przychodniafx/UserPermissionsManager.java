package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pl.example.przychodniafx.dao.ListPermissionsDAO;
import pl.example.przychodniafx.dao.RoleDAO;
import pl.example.przychodniafx.model.Permissions;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.model.UserPermission;
import pl.example.przychodniafx.DbConnection;

import java.sql.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPermissionsManager {

    @FXML private Label userNameLabel;
    @FXML private TableView<UserPermission> assignedPermissionsTable;
    @FXML private TableColumn<UserPermission, String> assignedPermissionNameColumn;
    @FXML private TableColumn<UserPermission, String> sourceColumn;
    @FXML private TableView<Permissions> availablePermissionsTable;
    @FXML private TableColumn<Permissions, String> availablePermissionNameColumn;
    @FXML private Button addPermissionButton;
    @FXML private Button removePermissionButton;
    @FXML private Label statusLabel;

    private final ListPermissionsDAO listPermissionsDAO = new ListPermissionsDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private User currentUser;

    private ObservableList<UserPermission> assignedPermissions = FXCollections.observableArrayList();
    private ObservableList<Permissions> availablePermissions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        assignedPermissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("permissionName"));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        availablePermissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("permission_name"));

        availablePermissionsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> addPermissionButton.setDisable(newSelection == null));
        assignedPermissionsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> removePermissionButton.setDisable(newSelection == null));
    }

    public void initData(User user) {
        this.currentUser = user;
        userNameLabel.setText(user.getFirstName() + " " + user.getLastName());

        loadUserPermissions();
        loadAvailablePermissions();
    }

    private void loadUserPermissions() {
        try {
            List<UserPermission> permissions = listPermissionsDAO.getUserPermissions(currentUser.getId());
            assignedPermissions.setAll(permissions);
            assignedPermissionsTable.setItems(assignedPermissions);

            if (permissions.isEmpty()) {
                showStatus("Użytkownik nie posiada żadnych uprawnień.", false);
            }
        } catch (SQLException e) {
            showAlert("Błąd", "Nie udało się załadować uprawnień użytkownika: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadAvailablePermissions() {
        try {
            List<Permissions> allPermissions = roleDAO.getAllPermissions();
            List<Integer> assignedPermissionIds = assignedPermissions.stream()
                    .map(UserPermission::getPermissionId)
                    .collect(Collectors.toList());

            List<Permissions> unassignedPermissions = allPermissions.stream()
                    .filter(permission -> !assignedPermissionIds.contains(permission.getPermission_id()))
                    .collect(Collectors.toList());

            availablePermissions.setAll(unassignedPermissions);
            availablePermissionsTable.setItems(availablePermissions);
        } catch (SQLException e) {
            showAlert("Błąd", "Nie udało się załadować dostępnych uprawnień: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddPermission() {
        Permissions selectedPermission = availablePermissionsTable.getSelectionModel().getSelectedItem();
        if (selectedPermission == null) return;

        try {
            String customRoleName = "Rola_" + currentUser.getId();
            int customRoleId = roleDAO.getOrCreateCustomRole(currentUser.getId(), customRoleName);

            roleDAO.assignPermissionToRole(customRoleId, selectedPermission.getPermission_id());

            if (!roleDAO.userHasRole(currentUser.getId(), customRoleId)) {
                roleDAO.assignRoleToUser(currentUser.getId(), customRoleId);
            }

            loadUserPermissions();
            loadAvailablePermissions();
            showStatus("Uprawnienie dodane pomyślnie.", true);
        } catch (SQLException e) {
            showAlert("Błąd", "Nie udało się dodać uprawnienia: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleRemovePermission() {
        UserPermission selectedPermission = assignedPermissionsTable.getSelectionModel().getSelectedItem();
        if (selectedPermission == null) return;

        String roleName = selectedPermission.getRoleName();
        if (roleName != null) {
            try {
                roleDAO.removePermissionFromCustomRole(
                        selectedPermission.getRoleId(),
                        selectedPermission.getPermissionId()
                );

                showStatus("Uprawnienie usunięte pomyślnie.", true);
                loadUserPermissions();
                loadAvailablePermissions();
            } catch (SQLException e) {
                showAlert("Błąd", "Nie udało się usunąć uprawnienia: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Informacja",
                    "Nie można usunąć uprawnienia przypisanego przez standardową rolę.\n" +
                            "Aby zmienić te uprawnienia, zmień rolę użytkownika.",
                    Alert.AlertType.INFORMATION);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) userNameLabel.getScene().getWindow();
        stage.close();
    }

    private void showStatus(String message, boolean isSuccess) {
        statusLabel.setText(message);
        statusLabel.setStyle(isSuccess ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static Set<String> getPermissionsForUser(Long userId) {
        Set<String> permissions = new HashSet<>();

        String sql = "SELECT DISTINCT p.permission_name " +
                "FROM permissions p " +
                "JOIN role_permissions rp ON p.permission_id = rp.permission_id " +
                "JOIN user_roles ur ON rp.role_id = ur.role_id " +
                "WHERE ur.user_id = ?";

        try (Connection conn = DbConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                permissions.add(rs.getString("permission_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return permissions;
    }
}
