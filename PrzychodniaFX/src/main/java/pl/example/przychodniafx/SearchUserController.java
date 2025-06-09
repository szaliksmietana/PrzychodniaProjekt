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
import javafx.scene.control.ListCell;
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

    @FXML
    public void initialize() {
        usersListView.setItems(usersObservableList);
        usersListView.setCellFactory(lv -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(user.getFirstName() + " " + user.getLastName() + " (PESEL: " + user.getPesel() + ")");
                }
            }
        });
        
        // Automatyczne wyszukiwanie przy wpisywaniu
        first_nameField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
        last_nameField.textProperty().addListener((observable, oldValue, newValue) -> handleSearch());
        
        detailsBox.setVisible(false);
        usersListBox.setVisible(false);
    }

    public void setInitialSearchData(String name, String surname) {
        first_nameField.setText(name);
        last_nameField.setText(surname);
    }

    @FXML
    private void handleSearch() {
        String firstName = first_nameField.getText().trim();
        String lastName = last_nameField.getText().trim();

        if (firstName.isEmpty() && lastName.isEmpty()) {
            resultLabel.setText("Wprowadź imię lub nazwisko.");
            hideAllPanels();
            return;
        }

        try {
            List<User> foundUsers = searchUserDAO.searchUsersByName(firstName, lastName, false);

            if (!foundUsers.isEmpty()) {
                foundUsers.sort((user1, user2) -> {
                    int score1 = calculateMatchScore(user1, firstName, lastName);
                    int score2 = calculateMatchScore(user2, firstName, lastName);
                    return score2 - score1;
                });
            }

            displaySearchResults(foundUsers);
        } catch (SQLException e) {
            e.printStackTrace();
            resultLabel.setText("Błąd podczas wyszukiwania: " + e.getMessage());
            hideAllPanels();
        }
    }

    private void displaySearchResults(List<User> foundUsers) {
        hideAllPanels();
        
        if (foundUsers.isEmpty()) {
            resultLabel.setText("Nie znaleziono użytkownika.");
        } else if (foundUsers.size() == 1) {
            foundUser = foundUsers.get(0);
            showUserDetails(foundUser);
        } else {
            usersObservableList.setAll(foundUsers);
            usersListBox.setVisible(true);
            usersListBox.setManaged(true);
            resultLabel.setText("Znaleziono " + foundUsers.size() + " użytkowników.");
        }
    }

    private void hideAllPanels() {
        detailsBox.setVisible(false);
        usersListBox.setVisible(false);
        usersListBox.setManaged(false);
    }

    private int calculateMatchScore(User user, String searchFirstName, String searchLastName) {
        int score = 0;
        
        if (!searchFirstName.isEmpty()) {
            String firstName = user.getFirstName().toLowerCase();
            searchFirstName = searchFirstName.toLowerCase();
            
            if (firstName.equals(searchFirstName)) {
                score += 100;
            } else if (firstName.startsWith(searchFirstName)) {
                score += 75;
            } else if (firstName.contains(searchFirstName)) {
                score += 50;
            }
        }
        
        if (!searchLastName.isEmpty()) {
            String lastName = user.getLastName().toLowerCase();
            searchLastName = searchLastName.toLowerCase();
            
            if (lastName.equals(searchLastName)) {
                score += 100;
            } else if (lastName.startsWith(searchLastName)) {
                score += 75;
            } else if (lastName.contains(searchLastName)) {
                score += 50;
            }
            

            score -= Math.abs(lastName.length() - searchLastName.length()) * 2;
        }
        
        return score;
    }

    @FXML
    private void handleUserSelection() {
        User selectedUser = usersListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            foundUser = selectedUser;
            showUserDetails(foundUser);
            usersListBox.setVisible(false);
            usersListBox.setManaged(false);
        }
    }

    private void showUserDetails(User user) {
        hideAllPanels();
        
        fullNameLabel.setText("Imię i Nazwisko: " + user.getFirstName() + " " + user.getLastName());
        birth_dateLabel.setText("Data urodzenia: " + user.getBirthDate());
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
                
                stage.setOnHidden(event -> refreshUserData());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void refreshUserData() {
        try {
            User refreshedUser = searchUserDAO.getUserById(foundUser.getId(), false);
            if (refreshedUser != null) {
                foundUser = refreshedUser;
                showUserDetails(foundUser);
            }
            if (usersListView.isVisible()) {
                handleSearch();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resultLabel.setText("Błąd podczas odświeżania danych: " + e.getMessage());
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) first_nameField.getScene().getWindow();
        stage.close();
    }
}