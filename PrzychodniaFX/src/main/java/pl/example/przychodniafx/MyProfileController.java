package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import pl.example.przychodniafx.model.Session;
import pl.example.przychodniafx.model.User;

public class MyProfileController {

    @FXML private Label nameLabel;
    @FXML private Label peselLabel;
    @FXML private Label birthDateLabel;
    @FXML private Label loginLabel;
    @FXML private Label genderLabel;

    @FXML
    public void initialize() {
        User user = Session.getUser();
        if (user == null) return;

        nameLabel.setText("Imię i nazwisko: " + user.getFirstName() + " " + user.getLastName());
        peselLabel.setText("PESEL: " + user.getPesel());
        birthDateLabel.setText("Data urodzenia: " + user.getBirthDate());
        loginLabel.setText("Login: " + user.getLogin());
        genderLabel.setText("Płeć: " + (user.getGender() != null && user.getGender() == 'M' ? "Mężczyzna" : "Kobieta"));
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }
}
