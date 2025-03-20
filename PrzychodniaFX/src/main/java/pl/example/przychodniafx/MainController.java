package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private TextField first_nameField;

    @FXML
    private TextField last_nameField;

    @FXML
    private void handleAddUser() {
        openWindow("AddUser.fxml", "Dodaj Użytkownika");
    }

    @FXML
    private void handleManageUsers() {
        openWindow("ManageUsers.fxml", "Zarządzanie Użytkownikami");
    }

    @FXML
    private void handleSearchUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SearchUser.fxml"));
            Parent root = loader.load();

            SearchUserController controller = loader.getController();

            if (first_nameField != null && last_nameField != null) {
                String name = first_nameField.getText().trim();
                String surname = last_nameField.getText().trim();
                controller.setInitialSearchData(name, surname);
            }

            Stage stage = new Stage();
            stage.setTitle("Wyszukaj użytkownika");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
