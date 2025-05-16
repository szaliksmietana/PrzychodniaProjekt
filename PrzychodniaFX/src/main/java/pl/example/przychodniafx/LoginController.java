package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.example.przychodniafx.dao.LoginDAO;
import pl.example.przychodniafx.model.User;
import java.io.IOException;


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

        try {
            User user = loginDAO.authenticate(login, password);
            if (user != null) {
                openMainPanel();
            } else {
                errorLabel.setText("Nieprawidłowy login lub hasło.");
            }
        } catch (Exception e) {
            errorLabel.setText("Błąd połączenia z bazą.");
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
            stage.setScene(new Scene(root, 400, 300));
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

            // Ustawienie rozmiaru
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);

            // Zamknięcie obecnego okna logowania
            Stage currentStage = (Stage) loginField.getScene().getWindow();
            currentStage.close();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
