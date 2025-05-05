package pl.example.przychodniafx.model;
import lombok.Data;

@Data
public class Roles {
    private Integer role_id;
    private String role_name;

    public Roles(String role_name){
        this.role_name = role_name;
    }

    @Override
    public String toString() {
        return role_name;
    }
}

