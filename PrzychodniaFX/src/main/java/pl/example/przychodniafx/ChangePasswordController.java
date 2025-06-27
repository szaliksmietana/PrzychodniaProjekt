package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.model.PasswordUtils;

public class ChangePasswordController {

    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label resultLabel;

    private User user;
    private final AddUserDAO userDAO = new AddUserDAO();

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void handleSave() {
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (newPass.isEmpty() || confirm.isEmpty()) {
            resultLabel.setText("Pola nie mogą być puste!");
            resultLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        if (!newPass.equals(confirm)) {
            resultLabel.setText("Hasła nie są zgodne!");
            resultLabel.setStyle("-fx-text-fill: red;");
            return;
        }


        String hashedPassword = PasswordUtils.hashPassword(newPass);

        try {

            userDAO.updateUserPassword(user.getId(), hashedPassword);

            resultLabel.setStyle("-fx-text-fill: green;");
            resultLabel.setText("Hasło zmienione pomyślnie!");
        } catch (Exception e) {
            resultLabel.setStyle("-fx-text-fill: red;");
            resultLabel.setText("Błąd: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) newPasswordField.getScene().getWindow();
        stage.close();
    }
}
