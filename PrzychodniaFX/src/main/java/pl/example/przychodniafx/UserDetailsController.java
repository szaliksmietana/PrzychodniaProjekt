package pl.example.przychodniafx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import pl.example.przychodniafx.model.User;

public class UserDetailsController {

    @FXML
    private Label nameLabel;
    @FXML
    private Label peselLabel;
    @FXML
    private Label birthDateLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label genderLabel;

    private User user;

    public void setUser(User user) {
        this.user = user;
        nameLabel.setText("Imię i nazwisko: " + user.getFirst_name() + " " + user.getLast_name());
        peselLabel.setText("PESEL: " + user.getPesel());
        birthDateLabel.setText("Data urodzenia: " + user.getBirth_date());
        //phoneLabel.setText("Telefon: " + (user.getPhone_number() != null ? user.getPhone_number() : "Brak"));
        genderLabel.setText("Płeć: " + (user.getGender() != null && user.getGender() == 'K' ? "Kobieta" : "Mężczyzna"));
    }

    @FXML
    private void handleEditUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditUser.fxml"));
            Parent root = loader.load();

            EditUserController controller = loader.getController();
            controller.setUserData(user);

            Stage stage = new Stage();
            stage.setTitle("Edytuj użytkownika");
            stage.setScene(new Scene(root, 400, 400));
            stage.show();

            // Zamknij to okno po otwarciu edycji
            Stage currentStage = (Stage) nameLabel.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleClose() {
        Stage stage = (Stage) nameLabel.getScene().getWindow();
        stage.close();
    }

}
