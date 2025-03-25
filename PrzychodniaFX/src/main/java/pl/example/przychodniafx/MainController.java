package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
//import pl.example.przychodniafx.controller.SearchUserController;

public class MainController {

    @FXML
    private TextField first_nameField;

    @FXML
    private TextField last_nameField;

    @FXML
    private void handleAddUser() {
        openWindow("/pl/example/przychodniafx/AddUser.fxml", "Dodaj usera");
    }

    @FXML
    private void handleManageUsers() {
        openWindow("/pl/example/przychodniafx/ManageUsers.fxml", "Zarządzanie Użytkownikami");
    }

    @FXML
    private void handleSearchUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/example/przychodniafx/SearchUser.fxml"));
            Parent root = loader.load();

            SearchUserController controller = loader.getController();

            // Pobieranie danych z pól tekstowych
            String name = first_nameField.getText().trim();
            String surname = last_nameField.getText().trim();

            controller.setInitialSearchData(name, surname);

            Stage stage = new Stage();
            stage.setTitle("Wyszukaj użytkownika");
            stage.setScene(new Scene(root, 450, 400));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 550, 400));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
