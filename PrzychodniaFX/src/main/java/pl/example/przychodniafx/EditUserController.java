package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pl.example.przychodniafx.model.User;

public class EditUserController {

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

    private User userToEdit;

    public void setUserData(User user) {
        this.userToEdit = user;
        first_nameField.setText(user.getFirst_name());
        last_nameField.setText(user.getLast_name());
        peselField.setText(user.getPesel());
        birth_dateField.setText(user.getBirth_date());
        phone_numberField.setText(user.getPhone_number());
    }

    @FXML
    private void handleSave() {
        if (userToEdit != null) {
            userToEdit.setFirst_name(first_nameField.getText());
            userToEdit.setLast_name(last_nameField.getText());
            userToEdit.setPesel(peselField.getText());
            userToEdit.setBirth_date(birth_dateField.getText());
            userToEdit.setPhone_number(phone_numberField.getText());

            System.out.println("Zapisano zmiany dla: " + userToEdit.getFirst_name() + " " + userToEdit.getLast_name());
        }
        handleCancel();
    }

    @FXML
    private void handleCancel() {
        Stage stage = (Stage) first_nameField.getScene().getWindow();
        stage.close();
    }
}
