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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UserPermissionsManager {

    @FXML
    private Label userNameLabel;

    @FXML
    private TableView<UserPermission> assignedPermissionsTable;

    @FXML
    private TableColumn<UserPermission, String> assignedPermissionNameColumn;

    @FXML
    private TableColumn<UserPermission, String> sourceColumn;

    @FXML
    private TableView<Permissions> availablePermissionsTable;

    @FXML
    private TableColumn<Permissions, String> availablePermissionNameColumn;

    @FXML
    private Button addPermissionButton;

    @FXML
    private Button removePermissionButton;

    @FXML
    private Label statusLabel;

    private final ListPermissionsDAO listPermissionsDAO = new ListPermissionsDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private User currentUser;

    private ObservableList<UserPermission> assignedPermissions = FXCollections.observableArrayList();
    private ObservableList<Permissions> availablePermissions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialize table columns
        assignedPermissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("permissionName"));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        availablePermissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("permission_name"));

        // Set up selection listener for enabling/disabling buttons
        availablePermissionsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> addPermissionButton.setDisable(newSelection == null));

        assignedPermissionsTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> removePermissionButton.setDisable(newSelection == null));
    }

    public void initData(User user) {
        this.currentUser = user;
        userNameLabel.setText(user.getFirst_name() + " " + user.getLast_name());

        loadUserPermissions();
        loadAvailablePermissions();
    }

    private void loadUserPermissions() {
        try {
            List<UserPermission> permissions = listPermissionsDAO.getUserPermissions(currentUser.getUser_id());
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
            // Get all permissions
            List<Permissions> allPermissions = roleDAO.getAllPermissions();

            // Get currently assigned permission IDs
            List<Integer> assignedPermissionIds = assignedPermissions.stream()
                    .map(UserPermission::getPermissionId)
                    .collect(Collectors.toList());

            // Filter out permissions that are already assigned
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
            // Create a custom role name for this user's custom permissions
            String customRoleName = "Rola_" + currentUser.getUser_id();
            int customRoleId = roleDAO.getOrCreateCustomRole(currentUser.getUser_id(), customRoleName);

            // Assign permission to the custom role
            roleDAO.assignPermissionToRole(customRoleId, selectedPermission.getPermission_id());

            // Check if user already has the custom role assigned
            if (!roleDAO.userHasRole(currentUser.getUser_id(), customRoleId)) {
                roleDAO.assignRoleToUser(currentUser.getUser_id(), customRoleId);
            }

            // Refresh permissions lists
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

        // Check if this is a direct permission or comes from a role
        String roleName = selectedPermission.getRoleName();
        if (roleName != null) {
            try {
                roleDAO.removePermissionFromCustomRole(
                        selectedPermission.getRoleId(),
                        selectedPermission.getPermissionId()
                );

                showStatus("Uprawnienie usunięte pomyślnie.", true);

                // Refresh permissions lists
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
}