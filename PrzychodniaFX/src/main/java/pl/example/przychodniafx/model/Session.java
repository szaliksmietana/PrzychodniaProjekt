package pl.example.przychodniafx.model;

import java.util.Set;

public class Session {
    private static Long userId;
    private static Set<String> permissions;
    private static User user;

    public static void setSession(Long id, Set<String> perms) {
        userId = id;
        permissions = perms;
    }

    public static void setUser(User u) {
        user = u;
    }

    public static User getUser() {
        return user;
    }

    public static Long getUserId() {
        return userId;
    }

    public static Set<String> getPermissions() {
        return permissions;
    }

    public static void clear() {
        userId = null;
        permissions = null;
        user = null;
    }
}
