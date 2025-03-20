package pl.example.przychodniafx.model;
import lombok.Data;

@Data
public class Permissions {
    private Integer permission_id;
    private String permission_name;

    public Permissions(Integer permission_id, String permission_name) {
        this.permission_id = permission_id;
        this.permission_name = permission_name;
    }
}
