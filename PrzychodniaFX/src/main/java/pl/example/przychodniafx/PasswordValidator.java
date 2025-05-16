package pl.example.przychodniafx;

public class PasswordValidator {

    public static boolean isValid(String password) {
        if (password == null || password.length() < 12) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUppercase = true;
            else if (Character.isLowerCase(ch)) hasLowercase = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasUppercase && hasLowercase && hasDigit && hasSpecial;
    }
}
