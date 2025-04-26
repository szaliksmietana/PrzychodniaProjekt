package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pl.example.przychodniafx.dao.ListPermissionsDAO;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.model.UserPermission;

import java.sql.SQLException;
import java.util.List;

public class UserPermissionsViewController {

    @FXML
    private Label userNameLabel;
    
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
        // Initialize table columns
        permissionNameColumn.setCellValueFactory(new PropertyValueFactory<>("permissionName"));
        roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
    }
    
    public void initData(User user) {
        this.currentUser = user;
        userNameLabel.setText(user.getFirst_name() + " " + user.getLast_name());
        
        loadUserPermissions();
    }
    
    private void loadUserPermissions() {
        try {
            List<UserPermission> permissions = listPermissionsDAO.getUserPermissions(currentUser.getUser_id());
            permissionsTable.setItems(FXCollections.observableArrayList(permissions));
            
            if (permissions.isEmpty()) {
                showAlert("Informacja", "Użytkownik nie posiada żadnych uprawnień.", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Błąd", "Nie udało się załadować uprawnień użytkownika: " + e.getMessage(), Alert.AlertType.ERROR);
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