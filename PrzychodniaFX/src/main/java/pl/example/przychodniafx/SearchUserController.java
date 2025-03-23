package pl.example.przychodniafx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pl.example.przychodniafx.dao.SearchUserDAO;
import pl.example.przychodniafx.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
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
    private SearchUserDAO searchUserDAO = new SearchUserDAO();
    private List<User> users = new ArrayList<>();


    @FXML
    public void initialize() {
        if (resultLabel == null) {
            System.out.println("BŁĄD: `resultLabel` NIE JEST ZAINICJALIZOWANY!");
        } else {
            System.out.println(" `resultLabel` został poprawnie zainicjalizowany.");
        }
        
        // Hide details box initially
        if (detailsBox != null) {
            detailsBox.setVisible(false);
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
        
        try {
            // First try exact match
            foundUser = searchUserDAO.getUserByNameAndSurname(name, surname);
            
            if (foundUser == null) {
                // If no exact match, try similar names
                users = searchUserDAO.searchUsersByName(name, surname);
                
                if (!users.isEmpty()) {
                    // Take the first match
                    foundUser = users.get(0);
                }
            }
            
            if (foundUser != null) {
                fullNameLabel.setText("Imię i Nazwisko: " + foundUser.getFirst_name() + " " + foundUser.getLast_name());
                birth_dateLabel.setText("Data urodzenia: " + foundUser.getBirth_date());
                peselLabel.setText("PESEL: " + foundUser.getPesel());
                
                detailsBox.setVisible(true);
                resultLabel.setText("");
            } else {
                detailsBox.setVisible(false);
                resultLabel.setText("Nie znaleziono użytkownika.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultLabel.setText("Błąd podczas wyszukiwania: " + e.getMessage());
            detailsBox.setVisible(false);
        }
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
