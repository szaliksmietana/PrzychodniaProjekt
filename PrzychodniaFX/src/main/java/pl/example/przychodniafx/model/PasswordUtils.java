package pl.example.przychodniafx.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtils {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;
    private static final int DEFAULT_LENGTH = 12;

    private static final SecureRandom random = new SecureRandom();

    public static String generateSecurePassword(int length) {
        if (length < 12) {
            throw new IllegalArgumentException("Hasło musi mieć co najmniej 12 znaków.");
        }

        StringBuilder password = new StringBuilder();
        password.append(randomChar(UPPER));
        password.append(randomChar(LOWER));
        password.append(randomChar(DIGITS));
        password.append(randomChar(SPECIAL));

        for (int i = 4; i < length; i++) {
            password.append(randomChar(ALL));
        }

        return shuffle(password.toString());
    }

    public static String generateSecurePassword() {
        return generateSecurePassword(DEFAULT_LENGTH);
    }

    private static char randomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }

    private static String shuffle(String input) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }
        return new String(array);
    }

    public static boolean isSecure(String password) {
        if (password == null || password.length() < 12) return false;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{}|;:,.<>?].*");

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }


    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b)); // na hex
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Nie można zahaszować hasła", e);
        }
    }

    /*
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return hashPassword(plainPassword).equals(hashedPassword);
    }*/
}
