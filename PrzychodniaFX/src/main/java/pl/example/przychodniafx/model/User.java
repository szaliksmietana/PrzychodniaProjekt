package pl.example.przychodniafx.model;

import lombok.Data;

@Data
public class User {
    private Integer user_id;
    private String login;
    private String password;
    private String first_name;
    private String last_name;
    private String pesel;
    private String birth_date;
    private Character gender;
    private Boolean is_forgotten;
    private Integer access_level;
    private String roleName;

    public User() {
        // Konstruktor bezparametrowy
    }

    public User(String first_name, String last_name, String pesel, String birth_date) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.pesel = pesel;
        this.birth_date = birth_date;
    }

    @Override
    public String toString() {
        return first_name + " " + last_name + " (PESEL: " + pesel + ")";
    }
}
