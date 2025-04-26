package pl.example.przychodniafx.model;

import lombok.Data;

@Data
public class UserPermission {
    private Integer permissionId;
    private String permissionName;
    private Integer roleId;
    private String roleName;
    
    public UserPermission() {
    }
    
    public UserPermission(Integer permissionId, String permissionName, Integer roleId, String roleName) {
        this.permissionId = permissionId;
        this.permissionName = permissionName;
        this.roleId = roleId;
        this.roleName = roleName;
    }
} 