package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.model.User;

import java.sql.SQLException;

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

    private final AddUserDAO UserDAO = new AddUserDAO();

    @FXML
    public void initialize() {
        genderGroup = new ToggleGroup();
        MRadio.setToggleGroup(genderGroup);
        FRadio.setToggleGroup(genderGroup);
        MRadio.setSelected(true);
    }

    @FXML
    private void handleSave() {
        String name = first_nameField.getText();
        String surname = last_nameField.getText();
        String pesel = peselField.getText();
        String birthDate = birth_dateField.getText();
        Character gender = getSelectedGender();

        if (name.isEmpty() || surname.isEmpty() || pesel.isEmpty() || birthDate.isEmpty()) {
            showErrorMessage("Wszystkie pola muszą być wypełnione!");
            return;
        }

        // Walidacja długości i formatu PESEL
        if (pesel.length() != 11 || !pesel.matches("\\d+")) {
            showErrorMessage("Błąd: Nieprawidłowy numer PESEL!");
            return;
        }

        // Tworzenie obiektu walidatora
        PeselValidator validator = new PeselValidator(pesel);

        // Sprawdzenie poprawności PESEL (checksum, miesiąc, dzień)
        if (!validator.isValid()) {
            showErrorMessage("Błąd: Nieprawidłowy numer PESEL!");
            return;
        }

        // Sprawdzenie zgodności daty z PESEL-em
        if (!validator.matchesBirthDate(birthDate)) {
            showErrorMessage("Błąd: PESEL nie zgadza się z datą urodzenia!");
            return;
        }
        if (!validator.matchesGender(gender)) {
            showErrorMessage("Błąd: PESEL nie zgadza się z płcią!");
            return;
        }


        try {
            // Ukryty komunikat przy duplikacie peselu
            if (UserDAO.isPeselExists(pesel)) {
                showErrorMessage("Błąd: Istnieje user z takim samym peselem!");
                return;
            }

            User user = new User(name, surname, pesel, birthDate);
            user.setGender(gender);
            user.setLogin(pesel);
            user.setPassword(pesel);
            user.setAccess_level(1);

            UserDAO.addUser(user);

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
