package pl.example.przychodniafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;


import java.util.List;

public class SearchUserController {

    @FXML
    private TextField nameField;

    @FXML
    private TextField surnameField;

    @FXML
    private Label resultLabel;

    @FXML
    private VBox detailsBox;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label birthDateLabel;

    @FXML
    private Label peselLabel;

    @FXML
    private Label phoneLabel;

    private User foundUser = null;

    private List<User> users = List.of(
            new User("Jan", "Kowalski", "12345678901", "1990-05-15", "500123456"),
            new User("Anna", "Nowak", "09876543210", "1985-08-20", "600987654")
    );


    @FXML
    public void initialize() {
        if (resultLabel == null) {
            System.out.println("BŁĄD: `resultLabel` NIE JEST ZAINICJALIZOWANY!");
        } else {
            System.out.println(" `resultLabel` został poprawnie zainicjalizowany.");
        }
    }


    public void setInitialSearchData(String name, String surname) {
        if (resultLabel == null) {
            System.out.println(" BŁĄD: `resultLabel` nadal jest NULL. FXML mogło nie zostać załadowane!");
            return;
        }

        nameField.setText(name);
        surnameField.setText(surname);


        if (!name.isEmpty() && !surname.isEmpty()) {
            handleSearch();
        }
    }

    @FXML
    private void handleSearch() {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();

        if (name.isEmpty() || surname.isEmpty()) {
            resultLabel.setText("Wprowadź imię i nazwisko!");
            detailsBox.setVisible(false);
            return;
        }

        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name) && user.getSurname().equalsIgnoreCase(surname)) {
                foundUser = user;
                fullNameLabel.setText("Imię i Nazwisko: " + user.getName() + " " + user.getSurname());
                birthDateLabel.setText("Data urodzenia: " + user.getBirthDate());
                peselLabel.setText("PESEL: " + user.getPesel());
                phoneLabel.setText("Telefon: " + user.getPhone());

                detailsBox.setVisible(true);
                resultLabel.setText("");
                return;
            }
        }

        foundUser = null;
        detailsBox.setVisible(false);
        resultLabel.setText("Nie znaleziono użytkownika.");
    }

    @FXML
    private void handleEdit() {
        if (foundUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("EditUser.fxml"));
                Parent root = loader.load();

                EditUserController controller = loader.getController();
                controller.setUserData(foundUser);

                Stage stage = new Stage();
                stage.setTitle("Edytuj użytkownika");
                stage.setScene(new Scene(root, 400, 400));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    private void handleClose() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }
}
