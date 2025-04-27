package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.dao.RoleDAO;
import pl.example.przychodniafx.model.Roles;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.model.UserPermission;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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

    private final AddUserDAO UserDAO = new AddUserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        MRadio.setToggleGroup(genderGroup);
        FRadio.setToggleGroup(genderGroup);
        MRadio.setSelected(true);

        try {
            List<Roles> roles = roleDAO.getAllRoles();
            ObservableList<Roles> rolesList = FXCollections.observableArrayList(roles);
            roleComboBox.setItems(rolesList);

            roleComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<Roles>() {
                @Override
                protected void updateItem(Roles role, boolean empty) {
                    super.updateItem(role, empty);
                    if (empty || role == null) {
                        setText(null);
                    } else {
                        setText(role.getRole_name());
                    }
                }
            });

            roleComboBox.setButtonCell(new javafx.scene.control.ListCell<Roles>() {
                @Override
                protected void updateItem(Roles role, boolean empty) {
                    super.updateItem(role, empty);
                    if (empty || role == null) {
                        setText(null);
                    } else {
                        setText(role.getRole_name());
                    }
                }
            });

            if (!roles.isEmpty()) {
                roleComboBox.getSelectionModel().selectFirst();
                updatePermissionsLabel(roles.get(0).getRole_id());
            }

            roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldRole, newRole) -> {
                if (newRole != null) {
                    updatePermissionsLabel(newRole.getRole_id());
                }
            });

        } catch (SQLException e) {
            showErrorMessage("Błąd podczas ładowania ról: " + e.getMessage());
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
    private void handleSave() {
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

            // 1. Tworzymy nowego usera
            User user = new User(name, surname, pesel, birthDate);
            user.setGender(gender);
            user.setLogin(pesel);
            user.setPassword(pesel);
            user.setAccess_level(1);

            // 2. Dodajemy usera i pobieramy jego ID
            int newUserId = UserDAO.addUserAndReturnId(user);

            // 3. Przypisujemy rolę do nowego usera
            roleDAO.assignRoleToUser(newUserId, selectedRole.getRole_id());

            showSuccessMessage("Sukces: dodano użytkownika " + name + " " + surname);
            closeWindow();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Błąd: Nie udało się dodać użytkownika: " + e.getMessage());
        }
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
