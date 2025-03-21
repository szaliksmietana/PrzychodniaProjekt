package pl.example.przychodniafx.model;

import lombok.Data;

@Data
public class Addresses {
    private Integer address_id;
    private String city;
    private String postal_code;
    private String street;
    private String house_number;
    private String apartment_number;
}
