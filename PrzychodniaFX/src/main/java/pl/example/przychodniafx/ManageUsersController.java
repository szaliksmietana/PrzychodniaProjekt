package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;
import javafx.stage.Stage;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.dao.ForgetUserDAO;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManageUsersController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> first_nameColumn;

    @FXML
    private TableColumn<User, String> last_nameColumn;

    @FXML
    private TableColumn<User, String> peselColumn;

    @FXML
    private TableColumn<User, String> birth_dateColumn;

    @FXML
    private TableColumn<User, String> genderColumn;

    @FXML
    private TableColumn<User, Boolean> isForgottenColumn;
    
    @FXML
    private CheckBox showForgottenUsersCheckbox;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<User> forgottenUserList = FXCollections.observableArrayList();

    private final AddUserDAO UserDAO = new AddUserDAO();
    private final ForgetUserDAO forgetUserDAO = new ForgetUserDAO();

    @FXML
    public void initialize() {
        // Aktywujemy edytowalność
        userTable.setEditable(true);
        isForgottenColumn.setEditable(true); // Checkbox jest edytowalny

        // Kolumny tekstowe
        first_nameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        last_nameColumn.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        peselColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        birth_dateColumn.setCellValueFactory(new PropertyValueFactory<>("birth_date"));

        // Gender jako string
        genderColumn.setCellValueFactory(cellData -> {
            Character gender = cellData.getValue().getGender();
            String display = "-";
            if (gender != null) {
                display = gender.equals('M') ? "Mężczyzna" : "Kobieta";
            }
            return new SimpleStringProperty(display);
        });

        // Checkbox kolumna: is_forgotten - wybór użytkownika do zapomnienia
        isForgottenColumn.setCellValueFactory(param -> {
            User user = param.getValue();
            SimpleBooleanProperty property = new SimpleBooleanProperty(user.getIs_forgotten() != null && user.getIs_forgotten());
            property.addListener((obs, oldVal, newVal) -> {
                if (newVal && !oldVal) { // Zaznaczono checkbox
                    user.setIs_forgotten(true);
                } else if (!newVal && oldVal) { // Odznaczono checkbox
                    user.setIs_forgotten(false);
                }
            });
            return property;
        });
        isForgottenColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isForgottenColumn));
        
        // Add listener for showForgottenUsersCheckbox if it exists in the FXML
        if (showForgottenUsersCheckbox != null) {
            showForgottenUsersCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    loadForgottenUsersFromDB();
                } else {
                    LoadUsersFromDB();
                }
            });
        }

        LoadUsersFromDB();
    }
    
    @FXML
    private void toggleForgottenUsers() {
        if (showForgottenUsersCheckbox.isSelected()) {
            loadForgottenUsersFromDB();
        } else {
            LoadUsersFromDB();
        }
    }
    
    private void loadForgottenUsersFromDB() {
        try {
            List<User> users = UserDAO.getAllUsers();
            forgottenUserList.clear();
            
            // Pobierz liste ID zapomnianych użytkowników
            List<Integer> forgottenIds = forgetUserDAO.getAllForgottenUserIds();
            
            // Filtruj użytkowników, którzy są w tabeli forgottenUsers
            users.stream()
                .filter(user -> user.getIs_forgotten() != null && user.getIs_forgotten() 
                       || forgottenIds.contains(user.getUser_id()))
                .forEach(forgottenUserList::add);
                
            userTable.setItems(forgottenUserList);
            
            // Disable checkbox column when viewing forgotten users
            isForgottenColumn.setEditable(false);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się załadować listy zapomnianych użytkowników: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleForgetUserButton() {
        // Znajdź zaznaczonych użytkowników
        List<User> selectedUsers = userList.stream()
                .filter(user -> user.getIs_forgotten() != null && user.getIs_forgotten())
                .toList();

        if (selectedUsers.isEmpty()) {
            showAlert("Błąd", "Nie wybrano żadnego użytkownika do zapomnienia!", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText("Zapomnij użytkowników");
        alert.setContentText("Czy na pewno chcesz zapomnieć wybranych użytkowników?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                for (User user : selectedUsers) {
                    // Użyj nowego DAO do zapomnienia użytkownika
                    forgetUserDAO.forgetUser(user);
                    // Usuń użytkownika z widoku tabeli
                    userList.remove(user);
                }
                showAlert("Sukces", "Wybrani użytkownicy zostali zapomniani!", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Błąd", "Nie udało się zapomnieć użytkowników: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void LoadUsersFromDB() {
        try {
            List<User> users = UserDAO.getAllUsers();
            userList.clear();
            // Filtruj użytkowników, aby pokazywać tylko niezapomnianych
            users.stream()
                .filter(user -> user.getIs_forgotten() == null || !user.getIs_forgotten())
                .forEach(userList::add);
            userTable.setItems(userList);
            
            // Re-enable checkbox column when viewing regular users
            isForgottenColumn.setEditable(true);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się załadować listy użytkowników: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void openEditUserWindow() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Błąd", "Nie wybrano użytkownika do edycji!", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/example/przychodniafx/EditUser.fxml"));
            Parent root = loader.load();

            EditUserController controller = loader.getController();
            controller.setUserData(selectedUser);

            Stage stage = new Stage();
            stage.setTitle("Edytuj użytkownika");
            stage.setScene(new Scene(root, 600, 500));
            stage.show();

            stage.setOnHidden(event -> {
                LoadUsersFromDB();
                userTable.refresh();
            });

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Błąd", "Nie można otworzyć okna edycji użytkownika!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) userTable.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showStandardWindow(String title, Parent root) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 500));
        stage.show();
    }
}
