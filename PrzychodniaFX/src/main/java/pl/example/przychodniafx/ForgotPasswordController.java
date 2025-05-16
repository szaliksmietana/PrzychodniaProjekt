package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.model.PasswordUtils;
import pl.example.przychodniafx.model.User;

public class ForgotPasswordController {

    @FXML private TextField loginField;
    @FXML private TextField peselField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label statusLabel;

    private final AddUserDAO userDAO = new AddUserDAO();

    @FXML
    private void handleResetPassword() {
        String login = loginField.getText().trim();
        String pesel = peselField.getText().trim();
        String newPassword = newPasswordField.getText();

        if (login.isEmpty() || pesel.isEmpty() || newPassword.isEmpty()) {
            statusLabel.setText("Wszystkie pola są wymagane.");
            return;
        }

        if (!PasswordUtils.isSecure(newPassword)) {
            statusLabel.setText("Hasło musi mieć co najmniej 12 znaków i zawierać duże litery, małe litery, cyfry oraz znaki specjalne.");
            return;
        }

        try {
            User user = userDAO.getUserByPesel(pesel);
            if (user == null || !user.getLogin().equals(login)) {
                statusLabel.setText("Nie znaleziono użytkownika z podanym loginem i PESEL.");
                return;
            }

            userDAO.updateUserPassword(user.getUser_id(), newPassword);
            statusLabel.setText("Hasło zostało zaktualizowane.");
            statusLabel.setStyle("-fx-text-fill: green;");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Wystąpił błąd podczas resetowania hasła.");
        }
    }
}
