package pl.example.przychodniafx.model;

import lombok.Data;

@Data
public class Contacts {
    private Integer contact_id;
    private String email;
    private String phone_number;
}
