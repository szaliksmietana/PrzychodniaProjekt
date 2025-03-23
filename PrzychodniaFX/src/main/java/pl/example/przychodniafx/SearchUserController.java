package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
    
    @FXML
    private VBox usersListBox;
    
    @FXML
    private ListView<User> usersListView;
    
    private ObservableList<User> usersObservableList = FXCollections.observableArrayList();

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
        
        // Hide boxes initially
        if (detailsBox != null) {
            detailsBox.setVisible(false);
        }
        
        if (usersListBox != null) {
            usersListBox.setVisible(false);
        }
        
        if (usersListView != null) {
            usersListView.setItems(usersObservableList);
        }
    }


    public void setInitialSearchData(String name, String surname) {
        if (resultLabel == null) {
            System.out.println(" BŁĄD: `resultLabel` nadal jest NULL. FXML mogło nie zostać załadowane!");
            return;
        }

        first_nameField.setText(name);
        last_nameField.setText(surname);

        // Wyszukaj, jeśli podano przynajmniej jeden parametr
        if (!name.isEmpty() || !surname.isEmpty()) {
            handleSearch();
        }
    }

    @FXML
    private void handleSearch() {
        String name = first_nameField.getText().trim();
        String surname = last_nameField.getText().trim();

        if (name.isEmpty() && surname.isEmpty()) {
            resultLabel.setText("Wprowadź imię lub nazwisko do wyszukania!");
            detailsBox.setVisible(false);
            usersListBox.setVisible(false);
            return;
        }
        
        try {
            List<User> foundUsers = null;
            
            // Jeśli podano imię i nazwisko
            if (!name.isEmpty() && !surname.isEmpty()) {
                // Szukaj dokładnych dopasowań
                foundUsers = searchUserDAO.getAllUsersByNameAndSurname(name, surname);
                
                if (foundUsers.isEmpty()) {
                    // Jeśli nie ma dokładnych dopasowań, szukaj podobnych
                    foundUsers = searchUserDAO.searchUsersByName(name, surname);
                }
            } else {
                // Szukaj po samym imieniu lub samym nazwisku
                foundUsers = searchUserDAO.searchUsersByName(name, surname);
            }
            
            // Pokaż wyniki
            if (foundUsers.isEmpty()) {
                // Nie znaleziono użytkowników
                usersListBox.setVisible(false);
                detailsBox.setVisible(false);
                resultLabel.setText("Nie znaleziono użytkownika.");
            } else if (foundUsers.size() == 1) {
                // Znaleziono dokładnie jednego użytkownika
                foundUser = foundUsers.get(0);
                showUserDetails(foundUser);
                usersListBox.setVisible(false);
            } else {
                // Znaleziono wielu użytkowników
                usersObservableList.clear();
                usersObservableList.addAll(foundUsers);
                usersListBox.setVisible(true);
                detailsBox.setVisible(false);
                resultLabel.setText("Znaleziono " + foundUsers.size() + " użytkowników.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultLabel.setText("Błąd podczas wyszukiwania: " + e.getMessage());
            detailsBox.setVisible(false);
            usersListBox.setVisible(false);
        }
    }
    
    @FXML
    private void handleUserSelection() {
        User selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            foundUser = selectedUser;
            showUserDetails(foundUser);
        }
    }
    
    private void showUserDetails(User user) {
        fullNameLabel.setText("Imię i Nazwisko: " + user.getFirst_name() + " " + user.getLast_name());
        birth_dateLabel.setText("Data urodzenia: " + user.getBirth_date());
        peselLabel.setText("PESEL: " + user.getPesel());
        
        detailsBox.setVisible(true);
        resultLabel.setText("");
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
                
                // Dodajemy nasłuchiwacz na zamknięcie okna
                stage.setOnHidden(event -> {
                    try {
                        // Odświeżenie danych użytkownika
                        User refreshedUser = searchUserDAO.getUserById(foundUser.getUser_id());
                        if (refreshedUser != null) {
                            foundUser = refreshedUser;
                            showUserDetails(foundUser);
                        }
                        // Odświeżenie listy użytkowników jeśli jest widoczna
                        if (usersListView.isVisible()) {
                            usersListView.refresh();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        resultLabel.setText("Błąd podczas odświeżania danych: " + e.getMessage());
                    }
                });
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
