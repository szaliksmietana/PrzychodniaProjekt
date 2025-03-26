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

    //@FXML
    //private TextField phone_numberField;

    @FXML
    private RadioButton MRadio;

    @FXML
    private RadioButton FRadio;

    private ToggleGroup genderGroup;

    private final AddUserDAO UserDAO = new AddUserDAO();

    @FXML
    public void initialize() {
        // Create a toggle group for the radio buttons
        genderGroup = new ToggleGroup();
        MRadio.setToggleGroup(genderGroup);
        FRadio.setToggleGroup(genderGroup);

        // Set male as default selection
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

        //Walidacja numeru pesel
        if (!isValidPesel(pesel)) {
            showErrorMessage("Błąd: Nieprawidłowy numer PESEL!");
            return;
        }

        try {
            // Check if user with this PESEL already exists
            User existingUser = UserDAO.getUserByPesel(pesel);
            if (existingUser != null) {
                showErrorMessage("Błąd: Użytkownik z podanym numerem PESEL już istnieje!");
                return;
            }

            User user = new User(name, surname, pesel, birthDate);

            user.setGender(gender);
            // Set default login value (using PESEL as login)
            user.setLogin(pesel);
            // Set default password value (using PESEL as password)
            user.setPassword(pesel);
            // Set default access level (1 for basic user)
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
            return 'K';  // K for Kobieta (Female)
        } else {
            return 'M';  // M for Mężczyzna (Male)
        }
    }

    private boolean isValidPesel(String pesel) {
        // Funkcja do walidacji numeru pesel
        if (pesel == null || pesel.length() != 11) {
            return false;
        }

        for (char c : pesel.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
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
