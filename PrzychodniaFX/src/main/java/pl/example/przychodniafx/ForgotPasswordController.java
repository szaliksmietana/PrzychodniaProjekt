package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.dao.LoginDAO;
import pl.example.przychodniafx.model.PasswordUtils;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.email.EmailService;
import pl.example.przychodniafx.email.TemporaryPasswordManager;

public class ForgotPasswordController {

    @FXML private TextField loginField;
    @FXML private TextField peselField;
    @FXML private TextField emailField;
    @FXML private PasswordField tempPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label statusLabel;
    @FXML private Button sendTempPasswordButton;
    @FXML private Button resetPasswordButton;

    private final AddUserDAO userDAO = new AddUserDAO();
    private final LoginDAO loginDAO = new LoginDAO();
    private final EmailService emailService = new EmailService();

    @FXML
    private void handleSendTemporaryPassword() {
        String login = loginField.getText().trim();
        String pesel = peselField.getText().trim();
        String email = emailField.getText().trim();

        if (login.isEmpty() || pesel.isEmpty()) {
            statusLabel.setText("Wprowadź login i PESEL.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            // Sprawdź czy użytkownik istnieje
            User user = userDAO.getUserByPesel(pesel);
            if (user == null || !user.getLogin().equals(login)) {
                statusLabel.setText("Nie znaleziono użytkownika z podanym loginem i PESEL.");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }

            // Generuj hasło tymczasowe
            String temporaryPassword = TemporaryPasswordManager.generateTemporaryPassword(login);

            // Wyślij email
            boolean emailSent = emailService.sendTemporaryPassword(email, temporaryPassword);

            if (emailSent) {
                statusLabel.setText("Hasło tymczasowe zostało wysłane na email. Wygaśnie za 3 minuty.");
                statusLabel.setStyle("-fx-text-fill: green;");

                // Pokaż informacje o hasle tymczasowym (TYLKO DO TESTOW)
                System.out.println("TEMP PASSWORD dla " + login + ": " + temporaryPassword);

            } else {
                statusLabel.setText("Błąd podczas wysyłania emaila. Spróbuj ponownie.");
                statusLabel.setStyle("-fx-text-fill: red;");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Wystąpił błąd podczas generowania hasła tymczasowego.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    @FXML
    private void handleResetPassword() {
        String login = loginField.getText().trim();
        String pesel = peselField.getText().trim();
        String tempPassword = tempPasswordField.getText();
        String newPassword = newPasswordField.getText();

        if (login.isEmpty() || pesel.isEmpty() /*|| newPassword.isEmpty()*/) {
            statusLabel.setText("Wszystkie pola są wymagane.");
            return;
        }

        // Sprawdź czy hasło tymczasowe jest poprawne
        if (!TemporaryPasswordManager.validateTemporaryPassword(login, tempPassword)) {
            statusLabel.setText("Niepoprawne hasło tymczasowe lub hasło wygasło.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!PasswordUtils.isSecure(newPassword)) {
            statusLabel.setText("Nowe hasło musi mieć co najmniej 12 znaków i zawierać duże litery, małe litery, cyfry oraz znaki specjalne.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            User user = userDAO.getUserByPesel(pesel);
            if (user == null || !user.getLogin().equals(login)) {
                statusLabel.setText("Nie znaleziono użytkownika z podanym loginem i PESEL.");
                return;
            }

            userDAO.updateUserPassword(user.getUser_id(), newPassword);

            TemporaryPasswordManager.removeTemporaryPassword(login);

            statusLabel.setText("Hasło zostało pomyślnie zaktualizowane! Możesz się teraz zalogować nowym hasłem.");
            statusLabel.setStyle("-fx-text-fill: green;");

            // Wyczyść pola po udanej zmianie
            tempPasswordField.clear();
            newPasswordField.clear();

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Wystąpił błąd podczas resetowania hasła.");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
}
