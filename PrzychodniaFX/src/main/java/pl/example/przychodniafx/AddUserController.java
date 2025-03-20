package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
    private TextField phone_numberField;

    @FXML
    private void handleSave() {

        String name = first_nameField.getText();
        String surname = last_nameField.getText();
        String pesel = peselField.getText();
        String birthDate = birth_dateField.getText();
        String phone = phone_numberField.getText();


        if (name.isEmpty() || surname.isEmpty() || pesel.isEmpty() || birthDate.isEmpty() || phone.isEmpty()) {
            System.out.println("Wszystkie pola muszą być wypełnione!");
            return;
        }

        // Tu można dodać zapis do bazy danych
        System.out.println("Dodano użytkownika: " + name + " " + surname);


        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) first_nameField.getScene().getWindow();
        stage.close();
    }
}

