package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.dao.RoleDAO;
import pl.example.przychodniafx.model.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class AddUserController {

    @FXML private TextField first_nameField;
    @FXML private TextField last_nameField;
    @FXML private TextField peselField;
    @FXML private TextField birth_dateField;
    @FXML private RadioButton MRadio;
    @FXML private RadioButton FRadio;
    @FXML private Label resultLabel;
    @FXML private ComboBox<Roles> roleComboBox;
    @FXML private Label permissionsLabel;
    @FXML private CheckBox customPermissionsCheckBox;
    @FXML private VBox permissionsContainer;
    @FXML private VBox permissionsCheckboxContainer;

    // Nowe pola:
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Button generatePasswordButton;

    private ToggleGroup genderGroup;
    private final AddUserDAO UserDAO = new AddUserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final Map<Integer, CheckBox> permissionCheckboxes = new HashMap<>();

    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        MRadio.setToggleGroup(genderGroup);
        FRadio.setToggleGroup(genderGroup);
        MRadio.setSelected(true);

        try {
            List<Roles> roles = roleDAO.getAllRoles().stream()
                    .filter(role -> !role.getRole_name().startsWith("Custom_"))
                    .collect(Collectors.toList());
            roleComboBox.setItems(FXCollections.observableArrayList(roles));

            if (!roles.isEmpty()) {
                roleComboBox.getSelectionModel().selectFirst();
                updatePermissionsLabel(roles.getFirst().getRole_id());
            }

            roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldRole, newRole) -> {
                if (newRole != null) updatePermissionsLabel(newRole.getRole_id());
            });

            customPermissionsCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                permissionsContainer.setVisible(newVal);
                permissionsContainer.setManaged(newVal);
                if (newVal && permissionCheckboxes.isEmpty()) loadAllPermissions();
                Roles selectedRole = roleComboBox.getSelectionModel().getSelectedItem();
                if (selectedRole != null) updatePermissionCheckboxes(selectedRole.getRole_id());
            });

        } catch (SQLException e) {
            showErrorMessage("Błąd ładowania ról: " + e.getMessage());
        }
    }

    private void loadAllPermissions() {
        try {
            List<Permissions> allPermissions = roleDAO.getAllPermissions();
            permissionsCheckboxContainer.getChildren().clear();
            permissionCheckboxes.clear();

            for (Permissions p : allPermissions) {
                CheckBox cb = new CheckBox(p.getPermission_name());
                permissionCheckboxes.put(p.getPermission_id(), cb);
                permissionsCheckboxContainer.getChildren().add(cb);
            }
        } catch (SQLException e) {
            showErrorMessage("Błąd ładowania uprawnień: " + e.getMessage());
        }
    }

    private void updatePermissionCheckboxes(int roleId) {
        try {
            permissionCheckboxes.values().forEach(cb -> cb.setSelected(false));
            List<UserPermission> rolePerms = roleDAO.getPermissionsForRole(roleId);
            for (UserPermission p : rolePerms) {
                CheckBox cb = permissionCheckboxes.get(p.getPermissionId());
                if (cb != null) cb.setSelected(true);
            }
        } catch (SQLException e) {
            showErrorMessage("Błąd aktualizacji uprawnień: " + e.getMessage());
        }
    }

    private void updatePermissionsLabel(int roleId) {
        try {
            List<UserPermission> perms = roleDAO.getPermissionsForRole(roleId);
            String text = perms.stream().map(UserPermission::getPermissionName).collect(Collectors.joining(", "));
            permissionsLabel.setText(text);
        } catch (SQLException e) {
            permissionsLabel.setText("Nie można załadować uprawnień");
        }
    }

    @FXML
    private void handleGeneratePassword() {
        String password = PasswordUtils.generateSecurePassword(12);
        passwordField.setText(password);
    }
    @FXML
    private void handleCancel() {
        closeWindow();
    }


    @FXML
    private void handleSave() {
        String name = first_nameField.getText();
        String surname = last_nameField.getText();
        String pesel = peselField.getText();
        String birthDate = birth_dateField.getText();
        Character gender = getSelectedGender();
        String login = loginField.getText();
        String password = passwordField.getText();
        Roles selectedRole = roleComboBox.getSelectionModel().getSelectedItem();

        if (name.isEmpty() || surname.isEmpty() || pesel.isEmpty() || birthDate.isEmpty()
                || login.isEmpty() || password.isEmpty() || selectedRole == null) {
            showErrorMessage("Wszystkie pola muszą być wypełnione!");
            return;
        }

        PeselValidator validator = new PeselValidator(pesel);
        if (!validator.isValid()) {
            showErrorMessage("Nieprawidłowy PESEL.");
            return;
        }

        if (!validator.matchesBirthDate(birthDate)) {
            showErrorMessage("Data urodzenia niezgodna z PESEL.");
            return;
        }

        if (!validator.matchesGender(gender)) {
            showErrorMessage("Płeć niezgodna z PESEL.");
            return;
        }

        try {
            if (UserDAO.isPeselExists(pesel)) {
                showErrorMessage("Użytkownik z tym PESEL już istnieje.");
                return;
            }

            User user = new User(name, surname, pesel, birthDate);
            user.setGender(gender);
            user.setLogin(login);
            user.setPassword(password);

            int newUserId = UserDAO.addUserAndReturnId(user);
            roleDAO.assignRoleToUser(newUserId, selectedRole.getRole_id());

            if (customPermissionsCheckBox.isSelected()) {
                List<UserPermission> defaultPerms = roleDAO.getPermissionsForRole(selectedRole.getRole_id());
                Set<Integer> defaultIds = defaultPerms.stream()
                        .map(UserPermission::getPermissionId).collect(Collectors.toSet());
                List<Integer> selected = getSelectedPermissions().stream()
                        .filter(id -> !defaultIds.contains(id))
                        .collect(Collectors.toList());
                int customRoleId = roleDAO.getOrCreateCustomRole(newUserId, "Rola_" + newUserId);
                for (int pid : selected) {
                    roleDAO.assignPermissionToRole(customRoleId, pid);
                }
            }

            showSuccessMessage("Dodano użytkownika " + name + " " + surname);
            closeWindow();

        } catch (SQLException e) {
            showErrorMessage("Błąd dodawania użytkownika: " + e.getMessage());
        }
    }

    private List<Integer> getSelectedPermissions() {
        return permissionCheckboxes.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private Character getSelectedGender() {
        return FRadio.isSelected() ? 'K' : 'M';
    }

    private void closeWindow() {
        Stage stage = (Stage) first_nameField.getScene().getWindow();
        stage.close();
    }

    private void showErrorMessage(String msg) {
        resultLabel.setStyle("-fx-text-fill: red;");
        resultLabel.setText(msg);
    }

    private void showSuccessMessage(String msg) {
        resultLabel.setStyle("-fx-text-fill: green;");
        resultLabel.setText(msg);
    }
}
