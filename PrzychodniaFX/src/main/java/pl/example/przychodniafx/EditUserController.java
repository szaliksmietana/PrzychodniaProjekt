package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class EditUserController {

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

    private User userToEdit;

    public void setUserData(User user) {
        this.userToEdit = user;
        nameField.setText(user.getName());
        surnameField.setText(user.getSurname());
        peselField.setText(user.getPesel());
        birthDateField.setText(user.getBirthDate());
        phoneField.setText(user.getPhone());
    }

    @FXML
    private void handleSave() {
        if (userToEdit != null) {
            userToEdit.setName(nameField.getText());
            userToEdit.setSurname(surnameField.getText());
            userToEdit.setPesel(peselField.getText());
            userToEdit.setBirthDate(birthDateField.getText());
            userToEdit.setPhone(phoneField.getText());

            System.out.println("Zapisano zmiany dla: " + userToEdit.getName() + " " + userToEdit.getSurname());
        }
        handleCancel();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
