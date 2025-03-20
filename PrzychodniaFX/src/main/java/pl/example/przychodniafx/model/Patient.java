package pl.example.przychodniafx.model;

import lombok.Data;

@Data
public class Patient {
    private Integer patient_id;
    private String first_name;
    private String last_name;
    private String pesel;
    private String birth_date;
    private Character gender;
    private String email;
    private String phone_number;
    private String city;
    private String postal_code;
    private String street;
    private String house_number;
    private String apartment_number;


    public Patient() {}

    public Patient(String first_name, String last_name, String pesel) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.pesel = pesel;
    }

}
