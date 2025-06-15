package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

public class AuthStartController {

    @FXML
    private void handleLoginButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/pl/example/przychodniafx/Login.fxml"));
            Stage stage = (Stage) getCurrentStage();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/pl/example/przychodniafx/Register.fxml"));
            Stage stage = (Stage) getCurrentStage();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Stage getCurrentStage() {
        return (Stage) Stage.getWindows().stream()
                .filter(Window -> Window.isShowing())
                .findFirst()
                .orElse(null);
    }
}
