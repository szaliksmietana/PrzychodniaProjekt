package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import pl.example.przychodniafx.model.User;
import pl.example.przychodniafx.dao.AddUserDAO;
import pl.example.przychodniafx.dao.ForgetUserDAO;
import pl.example.przychodniafx.dao.SearchUserDAO;
import javafx.scene.layout.HBox;


import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ManageUsersController {

    @FXML
    private TableView<User> userTable;

    @FXML
    private TableColumn<User, String> first_nameColumn;

    @FXML
    private TableColumn<User, String> last_nameColumn;

    @FXML
    private TableColumn<User, String> peselColumn;

    @FXML
    private TableColumn<User, String> birth_dateColumn;

    @FXML
    private TableColumn<User, String> genderColumn;

    @FXML
    private TableColumn<User, String> roleColumn;

    @FXML
    private TableColumn<User, Boolean> isForgottenColumn;

    @FXML
    private CheckBox showForgottenUsersCheckbox;

    @FXML
    private Button forgetUserButton;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private HBox searchBox;

    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<User> forgottenUserList = FXCollections.observableArrayList();

    private final AddUserDAO UserDAO = new AddUserDAO();
    private final ForgetUserDAO forgetUserDAO = new ForgetUserDAO();
    private final SearchUserDAO searchUserDAO = new SearchUserDAO();

    @FXML
    public void initialize() {
        userTable.setEditable(true);
        isForgottenColumn.setEditable(true);

        first_nameColumn.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        last_nameColumn.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        peselColumn.setCellValueFactory(new PropertyValueFactory<>("pesel"));
        birth_dateColumn.setCellValueFactory(new PropertyValueFactory<>("birth_date"));

        genderColumn.setCellValueFactory(cellData -> {
            Character gender = cellData.getValue().getGender();
            String display = "-";
            if (gender != null) {
                display = gender.equals('M') ? "Mężczyzna" : "Kobieta";
            }
            return new SimpleStringProperty(display);
        });

        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));


        isForgottenColumn.setCellValueFactory(param -> {
            User user = param.getValue();
            SimpleBooleanProperty property = new SimpleBooleanProperty(user.getIs_forgotten() != null && user.getIs_forgotten());
            property.addListener((obs, oldVal, newVal) -> {
                if (newVal && !oldVal) {
                    user.setIs_forgotten(true);
                } else if (!newVal && oldVal) {
                    user.setIs_forgotten(false);
                }
            });
            return property;
        });
        isForgottenColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isForgottenColumn));

        if (showForgottenUsersCheckbox != null) {
            showForgottenUsersCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    loadForgottenUsersFromDB();
                    forgetUserButton.setDisable(true);
                } else {
                    LoadUsersFromDB();
                    forgetUserButton.setDisable(false);
                }
            });
            forgetUserButton.setDisable(showForgottenUsersCheckbox.isSelected());
        }

        updateSearchFieldPlaceholder();
        LoadUsersFromDB();
    }

    private void updateSearchFieldPlaceholder() {
        if (searchField != null) {
            boolean isForgottenMode = showForgottenUsersCheckbox.isSelected();
            searchField.setPromptText(isForgottenMode ?
                    "Wyszukaj zapomnianego użytkownika (imię, nazwisko, rola)" :
                    "Wyszukaj aktywnego użytkownika (imię, nazwisko, rola)");
        }
    }

    private String mapAccessLevelToRoleName(Integer accessLevel) {
        if (accessLevel == null) return "-";
        return switch (accessLevel) {
            case 1 -> "Administrator";
            case 2 -> "Lekarz";
            case 3 -> "Pielęgniarka";
            case 4 -> "Recepcjonista";
            case 5 -> "Pacjent";
            default -> "Nieznana rola";
        };
    }

    @FXML
    private void toggleForgottenUsers() {
        if (showForgottenUsersCheckbox.isSelected()) {
            loadForgottenUsersFromDB();
            forgetUserButton.setDisable(true);
        } else {
            LoadUsersFromDB();
            forgetUserButton.setDisable(false);
        }
        updateSearchFieldPlaceholder();
        if (searchField != null) {
            searchField.clear();
        }
    }

    @FXML
    private void searchUsers() {
        String searchText = searchField.getText().trim().toLowerCase();
        boolean isForgottenMode = showForgottenUsersCheckbox.isSelected();

        try {
            List<User> allUsers = searchUserDAO.getAllUsers(isForgottenMode);

            List<User> foundUsers;
            if (searchText.isEmpty()) {
                foundUsers = allUsers;
            } else {
                foundUsers = allUsers.stream()
                        .filter(user ->
                                (user.getFirst_name() != null && user.getFirst_name().toLowerCase().contains(searchText)) ||
                                        (user.getLast_name() != null && user.getLast_name().toLowerCase().contains(searchText)) ||
                                        (user.getRoleName() != null && user.getRoleName().toLowerCase().contains(searchText))
                        )
                        .toList();
            }

            updateUsersList(foundUsers, isForgottenMode);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się wyszukać użytkowników: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }





    private void updateUsersList(List<User> users, boolean isForgottenMode) {
        if (isForgottenMode) {
            forgottenUserList.clear();
            forgottenUserList.addAll(users);
            userTable.setItems(forgottenUserList);
        } else {
            userList.clear();
            userList.addAll(users);
            userTable.setItems(userList);
        }

        if (users.isEmpty()) {
            showAlert("Informacja", "Nie znaleziono użytkowników spełniających kryteria wyszukiwania.", Alert.AlertType.INFORMATION);
        }
    }

    private void loadForgottenUsersFromDB() {
        try {
            List<User> users = UserDAO.getAllUsers();
            forgottenUserList.clear();

            List<Integer> forgottenIds = forgetUserDAO.getAllForgottenUserIds();

            users.stream()
                    .filter(user -> user.getIs_forgotten() != null && user.getIs_forgotten() || forgottenIds.contains(user.getUser_id()))
                    .forEach(forgottenUserList::add);

            userTable.setItems(forgottenUserList);
            isForgottenColumn.setEditable(false);
            forgetUserButton.setDisable(true);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się załadować zapomnianych użytkowników: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleForgetUserButton() {
        List<User> selectedUsers = userList.stream()
                .filter(user -> user.getIs_forgotten() != null && user.getIs_forgotten())
                .toList();

        if (selectedUsers.isEmpty()) {
            showAlert("Błąd", "Nie wybrano żadnego użytkownika do zapomnienia!", Alert.AlertType.WARNING);
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Potwierdzenie");
        alert.setHeaderText("Zapomnij użytkowników");
        alert.setContentText("Czy na pewno chcesz zapomnieć wybranych użytkowników?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                for (User user : selectedUsers) {
                    forgetUserDAO.forgetUser(user);
                    userList.remove(user);
                }
                showAlert("Sukces", "Wybrani użytkownicy zostali zapomniani!", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Błąd", "Nie udało się zapomnieć użytkowników: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void LoadUsersFromDB() {
        try {
            List<User> users = UserDAO.getAllUsers();
            userList.clear();
            users.stream()
                    .filter(user -> user.getIs_forgotten() == null || !user.getIs_forgotten())
                    .forEach(userList::add);
            userTable.setItems(userList);
            isForgottenColumn.setEditable(true);
            forgetUserButton.setDisable(false);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Błąd", "Nie udało się załadować listy użytkowników: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handlePreviewUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert("Błąd", "Nie wybrano użytkownika do podglądu!", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserDetails.fxml"));
            Parent root = loader.load();

            UserDetailsController controller = loader.getController();
            controller.setUser(selectedUser);

            Stage stage = new Stage();
            stage.setTitle("Podgląd danych użytkownika");
            stage.setScene(new Scene(root, 400, 400));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Błąd", "Nie można otworzyć okna podglądu użytkownika!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) userTable.getScene().getWindow();
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
