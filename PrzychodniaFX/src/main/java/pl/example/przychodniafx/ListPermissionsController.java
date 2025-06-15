package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import pl.example.przychodniafx.dao.ListPermissionsDAO;
import pl.example.przychodniafx.model.Permissions;
import pl.example.przychodniafx.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ListPermissionsController {

    @FXML
    private ComboBox<Permissions> permissionComboBox;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private TableView<User> usersTable;
    
    @FXML
    private TableColumn<User, String> firstNameColumn;
    
    @FXML
    private TableColumn<User, String> lastNameColumn;
    
    @FXML
    private TableColumn<User, String> peselColumn;
    
    @FXML
    private TableColumn<User, String> birthDateColumn;
    
    @FXML
    private TableColumn<User, String> genderColumn;
    
    private final ListPermissionsDAO listPermissionsDAO = new ListPermissionsDAO();
    private ObservableList<User> usersList = FXCollections.observableArrayList();
    private List<User> allUsers;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    public void initialize() {

        permissionComboBox.setConverter(new javafx.util.StringConverter<Permissions>() {
            @Override
            public String toString(Permissions permission) {
                return permission != null ? permission.getPermission_name() : "";
            }
            
            @Override
            public Permissions fromString(String string) {
                return null;
            }
        });
        

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        peselColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
        roleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRoleName()));


        genderColumn.setCellValueFactory(cellData -> {
            Character gender = cellData.getValue().getGender();
            String display = "-";
            if (gender != null) {
                display = gender.equals('M') ? "Mężczyzna" : "Kobieta";
            }
            return new SimpleStringProperty(display);
        });
        

        loadPermissions();
    }
    
    private void loadPermissions() {
        try {
            List<Permissions> permissions = listPermissionsDAO.getAllPermissions();
            permissionComboBox.setItems(FXCollections.observableArrayList(permissions));
            
            if (!permissions.isEmpty()) {
                permissionComboBox.getSelectionModel().selectFirst();
                handlePermissionSelect();
            }
        } catch (SQLException e) {
            showAlert("Błąd", "Nie udało się załadować uprawnień: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handlePermissionSelect() {
        Permissions selectedPermission = permissionComboBox.getSelectionModel().getSelectedItem();
        if (selectedPermission != null) {
            loadUsersByPermission(selectedPermission.getPermission_id());
        }
    }
    
    private void loadUsersByPermission(int permissionId) {
        try {
            allUsers = listPermissionsDAO.getUsersByPermission(permissionId);
            usersList.setAll(allUsers);
            usersTable.setItems(usersList);
        } catch (SQLException e) {
            showAlert("Błąd", "Nie udało się załadować użytkowników: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            usersList.setAll(allUsers);
        } else {
            // Filter users by search text
            List<User> filteredUsers = allUsers.stream()
                    .filter(user ->
                            (user.getFirstName() != null && user.getFirstName().toLowerCase().contains(searchText)) ||
                                    (user.getLastName() != null && user.getLastName().toLowerCase().contains(searchText)) ||
                                    (user.getRoleName() != null && user.getRoleName().toLowerCase().contains(searchText))
                    )
                    .collect(Collectors.toList());

            usersList.setAll(filteredUsers);
        }
    }


    @FXML
    private void handleShowUserPermissions() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Informacja", "Proszę wybrać użytkownika z listy.", Alert.AlertType.INFORMATION);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pl/example/przychodniafx/UserPermissionsView.fxml"));
            Parent root = loader.load();
            
            UserPermissionsViewController controller = loader.getController();
            controller.initData(selectedUser);
            
            Stage stage = new Stage();
            stage.setTitle("Uprawnienia użytkownika");
            stage.setScene(new Scene(root, 400, 400));
            stage.show();
        } catch (IOException e) {
            showAlert("Błąd", "Nie udało się otworzyć okna uprawnień użytkownika: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) permissionComboBox.getScene().getWindow();
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