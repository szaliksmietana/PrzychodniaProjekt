package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.dao.EditUserDAO;

import java.sql.SQLException;

public class EditUserController {

    @FXML
    private TextField first_nameField;

    @FXML
    private TextField last_nameField;

    @FXML
    private TextField peselField;

    @FXML
    private TextField birth_dateField;

    //@FXML
    //private TextField phone_numberField;

    private User userToEdit;
    private final EditUserDAO editUserDAO = new EditUserDAO();

    public void setUserData(User user) {
        this.userToEdit = user;
        first_nameField.setText(user.getFirst_name());
        last_nameField.setText(user.getLast_name());
        peselField.setText(user.getPesel());
        birth_dateField.setText(user.getBirth_date());
        //phone_numberField.setText(user.getPhone_number());
    }

    @FXML
    private void handleSave() {
        if (userToEdit != null) {
            String name = first_nameField.getText();
            String surname = last_nameField.getText();
            String pesel = peselField.getText();
            String birthDate = birth_dateField.getText();

            if (name.isEmpty() || surname.isEmpty() || pesel.isEmpty() || birthDate.isEmpty()) {
                System.out.println("Błąd, Wszystkie pola muszą być wypełnione!");
                return;
            }

            // Validate PESEL
            if (!isValidPesel(pesel)) {
                System.out.println("Błąd, Nieprawidłowy numer PESEL!");
                return;
            }

            userToEdit.setFirst_name(name);
            userToEdit.setLast_name(surname);
            userToEdit.setPesel(pesel);
            userToEdit.setBirth_date(birthDate);

            try {
                editUserDAO.updateUser(userToEdit);
                System.out.println("Sukces, Zapisano zmiany dla: " + userToEdit.getFirst_name() + " " + userToEdit.getLast_name());
                handleCancel();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Błąd, Nie udało się zaktualizować danych użytkownika: " + e.getMessage());
            }
        }
    }

    private boolean isValidPesel(String pesel) {
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
    
    @FXML
    private void handleCancel() {
        Stage stage = (Stage) first_nameField.getScene().getWindow();
        stage.close();
    }
}
