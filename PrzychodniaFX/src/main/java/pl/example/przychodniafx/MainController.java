package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainController {

    @FXML
    private void handleAddUser() {
        openWindow("/pl/example/przychodniafx/AddUser.fxml", "Dodaj usera");
    }

    @FXML
    private void handleManageUsers() {
        openWindow("/pl/example/przychodniafx/ManageUsers.fxml", "Zarządzanie Użytkownikami");
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
