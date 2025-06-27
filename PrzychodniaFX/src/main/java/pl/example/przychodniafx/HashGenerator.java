package pl.example.przychodniafx;

import pl.example.przychodniafx.model.PasswordUtils;

public class HashGenerator {

    public static void main(String[] args) {
        String plainPassword = "admin";  // Hasło, które chcesz zahashować


        String hashedPassword = PasswordUtils.hashPassword(plainPassword);


        System.out.println("Wygenerowane hasło (hash): " + hashedPassword);
    }
}
