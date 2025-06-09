package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pl.example.przychodniafx.dao.ListPermissionsDAO;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.model.UserPermission;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class UserPermissionsViewController {

    @FXML
    private Label userNameLabel;

    @FXML
    private Label peselLabel;

    @FXML
    private Label loginLabel;

    @FXML
    private TableView<UserPermission> permissionsTable;

    @FXML
    private TableColumn<UserPermission, String> permissionNameColumn;

    @FXML
    private TableColumn<UserPermission, String> roleNameColumn;

    private final ListPermissionsDAO listPermissionsDAO = new ListPermissionsDAO();
    private User currentUser;

    @FXML
    public void initialize() {
        permissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("permissionName"));
        roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
    }

    public void initData(User user) {
        this.currentUser = user;

        String imie = user.getFirstName() != null ? user.getFirstName() : "";
        String nazwisko = user.getLastName() != null ? user.getLastName() : "";
        String pesel = user.getPesel() != null ? user.getPesel() : "-";
        String login = user.getLogin() != null ? user.getLogin() : "-";

        userNameLabel.setText("Imię i nazwisko: " + imie + " " + nazwisko);
        peselLabel.setText("PESEL: " + pesel);
        loginLabel.setText("Login: " + login);

        loadUserPermissions();
    }

    private void loadUserPermissions() {
        try {
            List<UserPermission> permissions = listPermissionsDAO.getUserPermissions(currentUser.getId());
            permissionsTable.setItems(FXCollections.observableArrayList(permissions));

            if (permissions.isEmpty()) {
                showAlert("Informacja", "Użytkownik nie posiada żadnych uprawnień.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Błąd", "Nie udało się załadować uprawnień użytkownika: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleManagePermissions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/example/przychodniafx/UserPermissionsManager.fxml"));
            Parent root = loader.load();

            UserPermissionsManager controller = loader.getController();
            controller.initData(currentUser);

            Stage stage = new Stage();
            stage.setTitle("Zarządzanie uprawnieniami użytkownika");
            stage.setScene(new Scene(root, 800, 500));
            stage.showAndWait();

            loadUserPermissions(); // odświeżenie po zamknięciu okna
        } catch (IOException e) {
            showAlert("Błąd", "Nie udało się otworzyć okna zarządzania uprawnieniami: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) userNameLabel.getScene().getWindow();
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
