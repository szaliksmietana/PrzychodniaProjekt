package pl.example.przychodniafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pl.example.przychodniafx.model.User;


import java.util.List;

public class SearchUserController {

    @FXML
    private TextField first_nameField;

    @FXML
    private TextField last_nameField;

    @FXML
    private Label resultLabel;

    @FXML
    private VBox detailsBox;

    @FXML
    private Label fullNameLabel;

    @FXML
    private Label birth_dateLabel;

    @FXML
    private Label peselLabel;

    @FXML
    private Label phone_numberLabel;

    private User foundUser = null;

    private List<User> users = List.of(
            //new User("Jan", "Kowalski", "12345678901", "1990-05-15", "500123456"),
            //new User("Anna", "Nowak", "09876543210", "1985-08-20", "600987654")
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

        first_nameField.setText(name);
        last_nameField.setText(surname);


        if (!name.isEmpty() && !surname.isEmpty()) {
            handleSearch();
        }
    }

    @FXML
    private void handleSearch() {
        String name = first_nameField.getText().trim();
        String surname = last_nameField.getText().trim();

        if (name.isEmpty() || surname.isEmpty()) {
            resultLabel.setText("Wprowadź imię i nazwisko!");
            detailsBox.setVisible(false);
            return;
        }

        for (User user : users) {
            if (user.getFirst_name().equalsIgnoreCase(name) && user.getLast_name().equalsIgnoreCase(surname)) {
                foundUser = user;
                fullNameLabel.setText("Imię i Nazwisko: " + user.getFirst_name() + " " + user.getLast_name());
                birth_dateLabel.setText("Data urodzenia: " + user.getBirth_date());
                peselLabel.setText("PESEL: " + user.getPesel());
                //phone_numberLabel.setText("Telefon: " + user.getPhone_number());

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
        Stage stage = (Stage) first_nameField.getScene().getWindow();
        stage.close();
    }
}
