package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Button;
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


            Scene scene;
            if (fxmlPath.contains("AddUser.fxml")) {
                scene = new Scene(root, 550, 620);
            } else if (fxmlPath.contains("ManageUsers.fxml")) {
                scene = new Scene(root, 800, 600);
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

    @FXML
    private Button logoutButton;

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/example/przychodniafx/AuthStart.fxml"));
            Parent root = loader.load();

            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();

            Stage stage = new Stage();
            stage.setTitle("Ekran startowy");
            stage.setScene(new Scene(root, 500, 500));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
