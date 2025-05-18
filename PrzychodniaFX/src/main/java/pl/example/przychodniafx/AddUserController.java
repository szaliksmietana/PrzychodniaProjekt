package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.dao.RoleDAO;
import pl.example.przychodniafx.model.Roles;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.model.UserPermission;
import pl.example.przychodniafx.model.Permissions;

import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class AddUserController {

    @FXML
    private TextField first_nameField;

    @FXML
    private TextField last_nameField;

    @FXML
    private TextField peselField;

    @FXML
    private TextField birth_dateField;

    @FXML
    private Label resultLabel;

    @FXML
    private RadioButton MRadio;

    @FXML
    private RadioButton FRadio;

    private ToggleGroup genderGroup;

    @FXML
    private ComboBox<Roles> roleComboBox;

    @FXML
    private Label permissionsLabel;

    @FXML
    private CheckBox customPermissionsCheckBox;

    @FXML
    private VBox permissionsContainer;

    @FXML
    private VBox permissionsCheckboxContainer;

    private final AddUserDAO UserDAO = new AddUserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
    private final Roles roles = new Roles();

    private final Map<Integer, CheckBox> permissionCheckboxes = new HashMap<>();


    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        MRadio.setToggleGroup(genderGroup);
        FRadio.setToggleGroup(genderGroup);
        MRadio.setSelected(true);

        try {
            List<Roles> roles = roleDAO.getAllRoles()
                    .stream()
                    .filter(role -> !role.getRole_name().startsWith("Custom_"))
                    .collect(Collectors.toList());
            ObservableList<Roles> rolesList = FXCollections.observableArrayList(roles);
            roleComboBox.setItems(rolesList);


            if (!roles.isEmpty()) {
                roleComboBox.getSelectionModel().selectFirst();
                updatePermissionsLabel(roles.getFirst().getRole_id());
            }

            roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldRole, newRole) -> {
                if (newRole != null) {
                    updatePermissionsLabel(newRole.getRole_id());
                }
            });

            customPermissionsCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                permissionsContainer.setVisible(newVal);
                permissionsContainer.setManaged(newVal);

                if (newVal && permissionCheckboxes.isEmpty()) {
                    loadAllPermissions();
                }

                Roles selectedRole = roleComboBox.getSelectionModel().getSelectedItem();
                if (selectedRole != null) {
                    updatePermissionCheckboxes(selectedRole.getRole_id());
                }
            });
        } catch (SQLException e) {
            showErrorMessage("Błąd podczas ładowania ról: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAllPermissions() {
        try {
            List<Permissions> allPermissions = roleDAO.getAllPermissions();

            permissionsCheckboxContainer.getChildren().clear();
            permissionCheckboxes.clear();

            for (Permissions permission : allPermissions) {
                CheckBox checkBox = new CheckBox(permission.getPermission_name());
                permissionCheckboxes.put(permission.getPermission_id(), checkBox);
                permissionsCheckboxContainer.getChildren().add(checkBox);
            }
        } catch (SQLException e) {
            showErrorMessage("Błąd podczas ładowania uprawnień: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePermissionCheckboxes(int roleId) {
        try {
            for (CheckBox checkBox : permissionCheckboxes.values()) {
                checkBox.setSelected(false);
            }

            List<UserPermission> rolePermissions = roleDAO.getPermissionsForRole(roleId);

            for (UserPermission permission : rolePermissions) {
                CheckBox checkBox = permissionCheckboxes.get(permission.getPermissionId());
                if (checkBox != null) {
                    checkBox.setSelected(true);
                }
            }
        } catch (SQLException e) {
            showErrorMessage("Błąd podczas aktualizacji uprawnień: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void updatePermissionsLabel(int roleId) {
        try {
            List<UserPermission> permissions = roleDAO.getPermissionsForRole(roleId);
            String permissionsText = permissions.stream()
                    .map(UserPermission::getPermissionName)
                    .collect(Collectors.joining(", "));
            permissionsLabel.setText(permissionsText);
        } catch (SQLException e) {
            permissionsLabel.setText("Nie można załadować uprawnień");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() throws SQLException {
        String name = first_nameField.getText();
        String surname = last_nameField.getText();
        String pesel = peselField.getText();
        String birthDate = birth_dateField.getText();
        Character gender = getSelectedGender();
        Roles selectedRole = roleComboBox.getSelectionModel().getSelectedItem();

        if (name.isEmpty() || surname.isEmpty() || pesel.isEmpty() || birthDate.isEmpty() || selectedRole == null) {
            showErrorMessage("Wszystkie pola muszą być wypełnione!");
            return;
        }

        if (pesel.length() != 11 || !pesel.matches("\\d+")) {
            showErrorMessage("Błąd: Nieprawidłowy numer PESEL!");
            return;
        }

        PeselValidator validator = new PeselValidator(pesel);

        if (!validator.isValid()) {
            showErrorMessage("Błąd: Nieprawidłowy numer PESEL!");
            return;
        }

        if (!validator.matchesBirthDate(birthDate)) {
            showErrorMessage("Błąd: PESEL nie zgadza się z datą urodzenia!");
            return;
        }

        if (!validator.matchesGender(gender)) {
            showErrorMessage("Błąd: PESEL nie zgadza się z płcią!");
            return;
        }

        try {
            if (UserDAO.isPeselExists(pesel)) {
                showErrorMessage("Błąd: Istnieje użytkownik z takim samym PESEL!");
                return;
            }

            User user = new User(name, surname, pesel, birthDate);
            user.setGender(gender);
            user.setLogin(pesel);
            user.setPassword(pesel);
            user.setAccess_level(1);

            int newUserId = UserDAO.addUserAndReturnId(user);

            roleDAO.assignRoleToUser(newUserId, selectedRole.getRole_id());

            if (customPermissionsCheckBox.isSelected()) {
                try {
                    List<UserPermission> rolePermissions = roleDAO.getPermissionsForRole(selectedRole.getRole_id());
                    List<Integer> rolePermissionIds = rolePermissions.stream()
                            .map(UserPermission::getPermissionId)
                            .collect(Collectors.toList());

                    List<Integer> allSelectedPermissions = getSelectedPermissions();

                    List<Integer> additionalPermissions = allSelectedPermissions.stream()
                            .filter(id -> !rolePermissionIds.contains(id))
                            .collect(Collectors.toList());
/*
                    if (!additionalPermissions.isEmpty()) {
                        roleDAO.assignDirectPermissionsToUser(newUserId, additionalPermissions);
                    }
*/
                } catch (SQLException ex) {
                   // List<Integer> selectedPermissions = getSelectedPermissions();
                 //   roleDAO.assignDirectPermissionsToUser(newUserId, selectedPermissions);
                }

                showSuccessMessage("Sukces: dodano użytkownika " + name + " " + surname);
                closeWindow();

            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Błąd: Nie udało się dodać użytkownika: " + e.getMessage());
        }
    }

    private List<Integer> getSelectedPermissions() {
        List<Integer> selectedPermissions = new ArrayList<>();

        for (Map.Entry<Integer, CheckBox> entry : permissionCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedPermissions.add(entry.getKey());
            }
        }

        return selectedPermissions;
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) first_nameField.getScene().getWindow();
        stage.close();
    }

    private Character getSelectedGender() {
        if (FRadio.isSelected()) {
            return 'K';
        } else {
            return 'M';
        }
    }

    private void showErrorMessage(String message) {
        if (resultLabel != null) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText(message);
        }
    }

    private void showSuccessMessage(String message) {
        if (resultLabel != null) {
            resultLabel.setStyle("-fx-text-fill: green;");
            resultLabel.setText(message);
        }
    }
}
