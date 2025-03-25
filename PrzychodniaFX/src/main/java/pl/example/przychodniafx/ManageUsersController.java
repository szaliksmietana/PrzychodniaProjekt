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
import pl.example.przychodniafx.dao.DeleteUserDAO;

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

    private ObservableList<User> userList = FXCollections.observableArrayList();

    private final AddUserDAO UserDAO = new AddUserDAO();
    private final DeleteUserDAO deleteUserDAO = new DeleteUserDAO();

    @FXML
    public void initialize() {
        // Aktywujemy edytowalność
        userTable.setEditable(true);
        isForgottenColumn.setEditable(true);

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

        // Checkbox kolumna: is_forgotten
        isForgottenColumn.setCellValueFactory(param -> {
            User user = param.getValue();
            SimpleBooleanProperty property = new SimpleBooleanProperty(user.getIs_forgotten() != null && user.getIs_forgotten());
            property.addListener((obs, oldVal, newVal) -> user.setIs_forgotten(newVal));
            return property;
        });
        isForgottenColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isForgottenColumn));

        LoadUsersFromDB();
    }

    private void LoadUsersFromDB() {
        try {
            List<User> users = UserDAO.getAllUsers();
            userList.clear();
            userList.addAll(users);
            userTable.setItems(userList);
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
            stage.setScene(new Scene(root, 600, 500)); // <-- zmieniony rozmiar
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
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Błąd", "Nie wybrano użytkownika do usunięcia!", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText("Usunięcie użytkownika");
        alert.setContentText("Czy na pewno chcesz usunąć użytkownika " + selectedUser.getFirst_name() + " " + selectedUser.getLast_name() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = deleteUserDAO.deleteUser(selectedUser);
                if (deleted) {
                    userList.remove(selectedUser);
                    showAlert("Sukces", "Użytkownik został usunięty!", Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Błąd", "Nie udało się usunąć użytkownika z bazy danych!", Alert.AlertType.ERROR);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Błąd", "Wystąpił problem podczas usuwania użytkownika: " + e.getMessage(), Alert.AlertType.ERROR);
            }
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
