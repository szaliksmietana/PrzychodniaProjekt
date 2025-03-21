package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.dao.EditUserDAO;

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

    //@FXML
    //private TableColumn<User, String> phone_numberColumn;

    private ObservableList<User> userList = FXCollections.observableArrayList(
            //new User("Jan", "Kowalski", "12345678901", "1990-05-15", "500123456"),
            //new User("Anna", "Nowak", "09876543210", "1985-08-20", "600987654")
    );
    private final AddUserDAO UserDAO = new AddUserDAO();

    @FXML
    public void initialize() {

        first_nameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        last_nameColumn.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        peselColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        birth_dateColumn.setCellValueFactory(new PropertyValueFactory<>("birth_date"));
        //phone_numberColumn.setCellValueFactory(new PropertyValueFactory<>("phone_number"));
        LoadUsersFromDB();
    }

    private void LoadUsersFromDB(){
        try{
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
            stage.setScene(new Scene(root, 400, 400));
            stage.show();
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
            userList.remove(selectedUser);
            showAlert("Sukces", "Użytkownik został usunięty!", Alert.AlertType.INFORMATION);
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

}
