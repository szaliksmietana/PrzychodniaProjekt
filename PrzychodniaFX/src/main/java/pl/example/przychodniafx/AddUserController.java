package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddUserController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private TextField peselField;

    @FXML
    private TextField birthDateField;

    @FXML
    private TextField phoneField;

    @FXML
    private void handleSave() {

        String name = nameField.getText();
        String surname = surnameField.getText();
        String pesel = peselField.getText();
        String birthDate = birthDateField.getText();
        String phone = phoneField.getText();


        if (name.isEmpty() || surname.isEmpty() || pesel.isEmpty() || birthDate.isEmpty() || phone.isEmpty()) {
            System.out.println("Wszystkie pola muszą być wypełnione!");
            return;
        }

        // Tu można dodać zapis do bazy danych
        User user = new User(name, surname, pesel, birthDate, phone);
        UserService.getInstance().addUser(user);

        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}

