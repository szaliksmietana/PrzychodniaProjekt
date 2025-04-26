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
    
    @FXML
    private void handleListPermissions() {
        openWindow("/pl/example/przychodniafx/ListPermissions.fxml", "Przegląd listy uprawnień");
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);

            // Dostosuj rozmiar w zależności od otwieranego FXML
            Scene scene;
            if (fxmlPath.contains("AddUser.fxml")) {
                scene = new Scene(root, 550, 500);
            } else if (fxmlPath.contains("ManageUsers.fxml")) {
                scene = new Scene(root, 600, 600);
            } else if (fxmlPath.contains("ListPermissions.fxml")) {
                scene = new Scene(root, 800, 600);
            } else {
                scene = new Scene(root, 600, 400);
            }

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
