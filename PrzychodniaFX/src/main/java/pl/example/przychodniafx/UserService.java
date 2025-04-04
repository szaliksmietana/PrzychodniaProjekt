package pl.example.przychodniafx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class UserService {
    private static UserService instance;
    private ObservableList<User> users;

    private UserService() {
        // Inicjalizacja przykładowych danych
        users = FXCollections.observableArrayList(
                new User("Jan", "Kowalski", "12345678901", "1990-05-15", "500123456"),
                new User("Anna", "Nowak", "09876543210", "1985-08-20", "600987654")
        );
    }

    public static synchronized UserService getInstance() {
        if (instance == null) {
            instance = new UserService();}
        return instance;}


    public ObservableList<User> getAllUsers() {
        return users;}


    public void addUser(User user) {
        users.add(user);
        System.out.println("Dodano użytkownika: " + user.getName() + " " + user.getSurname());
    }


    public void removeUser(User user) {
        users.remove(user);}


    public User findUser(String name, String surname) {
        for (User user : users) {
            if (user.getName().equalsIgnoreCase(name) &&
                    user.getSurname().equalsIgnoreCase(surname)) {
                return user;}}
        return null;}
}