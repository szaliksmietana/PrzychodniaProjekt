package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.example.przychodniafx.dao.LoginDAO;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.LoginAttemptManager;
import pl.example.przychodniafx.model.Session;
import pl.example.przychodniafx.UserPermissionsManager;

import java.io.IOException;
import java.util.Set;

public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private final LoginDAO loginDAO = new LoginDAO();

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Wprowadź login i hasło.");
            return;
        }

        if (LoginAttemptManager.isLoginBlocked(login)) {
            long remainingMinutes = LoginAttemptManager.getBlockTimeRemaining(login);
            errorLabel.setText("Konto zablokowane. Spróbuj ponownie za " + remainingMinutes + " minut.");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            User user = loginDAO.authenticate(login, password);
            if (user != null) {
                LoginAttemptManager.recordSuccessfulLogin(login);

                Set<String> permissions = UserPermissionsManager.getPermissionsForUser(user.getId().longValue());
                Session.setSession(user.getId().longValue(), permissions);
                Session.setUser(user);

                errorLabel.setText("Logowanie pomyślne!");
                errorLabel.setStyle("-fx-text-fill: green;");
                openMainPanel();
            } else {
                LoginAttemptManager.recordFailedAttempt(login);
                int remainingAttempts = LoginAttemptManager.getRemainingAttempts(login);
                if (remainingAttempts > 0) {
                    errorLabel.setText("Nieprawidłowy login lub hasło. Pozostało prób: " + remainingAttempts);
                } else {
                    errorLabel.setText("Konto zostało zablokowane na 5 minut po 3 nieudanych próbach.");
                }
                errorLabel.setStyle("-fx-text-fill: red;");
            }
        } catch (Exception e) {
            errorLabel.setText("Błąd połączenia z bazą.");
            errorLabel.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/example/przychodniafx/ForgotPassword.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Reset hasła");
            stage.setScene(new Scene(root, 400, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/pl/example/przychodniafx/AuthStart.fxml"));
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMainPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/example/przychodniafx/Main.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Panel Główny");
            stage.setScene(new Scene(root, 800, 600));

            Stage currentStage = (Stage) loginField.getScene().getWindow();
            currentStage.close();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
