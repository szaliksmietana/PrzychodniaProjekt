package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import pl.example.przychodniafx.model.User;

import java.io.IOException;

public class UserDetailsController {

    @FXML private Label nameLabel;
    @FXML private Label peselLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label genderLabel;
    @FXML private Label loginLabel;

    private User user;

    public void setUser(User user) {
        this.user = user;

        nameLabel.setText("Imię i nazwisko: " + user.getFirstName() + " " + user.getLastName());
        peselLabel.setText("PESEL: " + user.getPesel());
        birthDateLabel.setText("Data urodzenia: " + user.getBirthDate());
        loginLabel.setText("Login: " + (user.getLogin() != null ? user.getLogin() : "-"));

        String gender = "-";
        if (user.getGender() != null) {
            gender = user.getGender() == 'M' ? "Mężczyzna" : "Kobieta";
        }
        genderLabel.setText("Płeć: " + gender);
    }


    @FXML
    private void handleChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
            Parent root = loader.load();

            ChangePasswordController controller = loader.getController();
            controller.setUser(user);

            Stage stage = new Stage();
            stage.setTitle("Zmień hasło");
            stage.setScene(new Scene(root, 300, 250));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}
