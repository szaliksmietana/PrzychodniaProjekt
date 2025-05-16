package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.dao.RoleDAO;
import pl.example.przychodniafx.model.PasswordUtils;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.PeselValidator;
import javafx.scene.Parent;


import java.io.IOException;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField peselField;
    @FXML private TextField birthDateField;
    @FXML private RadioButton maleRadio;
    @FXML private RadioButton femaleRadio;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label passwordRequirementsLabel;
    @FXML private Label statusLabel;
    @FXML private Button registerButton;
    @FXML private Label generatedPasswordLabel;

    private final AddUserDAO addUserDAO = new AddUserDAO();
    private final RoleDAO roleDAO = new RoleDAO();

    @FXML
    private void handleRegister() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String pesel = peselField.getText().trim();
        String birthDate = birthDateField.getText().trim();
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        Character gender = maleRadio.isSelected() ? 'M' : 'K';

        if (firstName.isEmpty() || lastName.isEmpty() || pesel.isEmpty() ||
                birthDate.isEmpty() || login.isEmpty() || password.isEmpty()) {
            showError("Wszystkie pola są wymagane.");
            return;
        }

        PeselValidator validator = new PeselValidator(pesel);
        if (!validator.isValid()) {
            showError("PESEL nieprawidłowy.");
            return;
        }

        if (!validator.matchesBirthDate(birthDate)) {
            showError("Data urodzenia nie zgadza się z PESEL.");
            return;
        }

        if (!validator.matchesGender(gender)) {
            showError("Płeć nie zgadza się z PESEL.");
            return;
        }

        if (!PasswordUtils.isSecure(password)) {
            showError("Hasło nie spełnia wymagań bezpieczeństwa.");
            return;
        }

        try {
            if (addUserDAO.isPeselExists(pesel)) {
                showError("Użytkownik z tym PESEL już istnieje.");
                return;
            }

            User newUser = new User(firstName, lastName, pesel, birthDate);
            newUser.setGender(gender);
            newUser.setLogin(login);
            newUser.setPassword(password);
            newUser.setAccess_level(1);

            int userId = addUserDAO.addUserAndReturnId(newUser);
            roleDAO.assignRoleToUser(userId, 5); // 5 = Pacjent

            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Rejestracja zakończona sukcesem!");

            // Przejście do ekranu startowego
            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AuthStart.fxml"));
            Stage startStage = new Stage();
            startStage.setScene(new Scene(loader.load()));
            startStage.setTitle("Panel logowania");
            startStage.show();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showError("Błąd podczas rejestracji: " + e.getMessage());
        }
    }

    @FXML
    private void handleGeneratePassword() {
        String generated = PasswordUtils.generateSecurePassword();
        passwordField.setText(generated);
        generatedPasswordLabel.setText("Twoje hasło to: " + generated);
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: red;");
        statusLabel.setText(message);
    }
    @FXML
    private void handleCancel() {
        try {

            Stage stage = (Stage) registerButton.getScene().getWindow();
            stage.close();

            // Załaduj ekran startowy (AuthStart)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/example/przychodniafx/AuthStart.fxml"));
            Parent root = loader.load();

            Stage startStage = new Stage();
            startStage.setTitle("Panel logowania");
            startStage.setScene(new Scene(root, 400, 300));
            startStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
