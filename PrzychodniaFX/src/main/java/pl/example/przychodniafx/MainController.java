package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import pl.example.przychodniafx.model.Session;

import java.io.IOException;
import java.util.Set;

public class MainController {

    @FXML
    private Button addUserButton;

    @FXML
    private Button manageUsersButton;

    @FXML
    private Button listPermissionsButton;

    @FXML
    private Button logoutButton;

    @FXML
    private void initialize() {
        Set<String> permissions = Session.getPermissions();

        if (permissions == null || permissions.isEmpty()) {
            System.out.println("Brak uprawnień w sesji.");
            addUserButton.setVisible(false);
            manageUsersButton.setVisible(false);
            listPermissionsButton.setVisible(false);
            return;
        }

        System.out.println("Uprawnienia w sesji: " + permissions);

        if (!permissions.contains("Dodawanie pracownika")) {
            addUserButton.setVisible(false);
        }

        if (!permissions.contains("Zarządzanie pacjentem")) {
            manageUsersButton.setVisible(false);
        }

        if (!permissions.contains("Zarządzanie uprawnieniami")) {
            listPermissionsButton.setVisible(false);
        }
    }

    @FXML
    private void handleAddUser() {
        openWindow("/pl/example/przychodniafx/AddUser.fxml", "Dodaj użytkownika");
    }

    @FXML
    private void handleManageUsers() {
        openWindow("/pl/example/przychodniafx/ManageUsers.fxml", "Zarządzanie użytkownikami");
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
                scene = new Scene(root, 550, 780);
            } else if (fxmlPath.contains("ManageUsers.fxml") || fxmlPath.contains("ListPermissions.fxml")) {
                scene = new Scene(root, 800, 600);
            } else {
                scene = new Scene(root, 600, 400);
            }

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleShowProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/example/przychodniafx/MyProfile.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Moje dane");
            stage.setScene(new Scene(loader.load(), 400, 300));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
