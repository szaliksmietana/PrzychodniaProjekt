package pl.example.przychodniafx.email;

import lombok.Getter;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TemporaryPasswordManager {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ConcurrentHashMap<String, TemporaryPassword> temporaryPasswords = new ConcurrentHashMap<>();
    private static final int EXPIRY_MINUTES = 3;

    public static class TemporaryPassword {
        @Getter
        private final String password;
        private final LocalDateTime expiryTime;

        public TemporaryPassword(String password, LocalDateTime expiryTime) {
            this.password = password;
            this.expiryTime = expiryTime;
        }

        public boolean isExpired() { return LocalDateTime.now().isAfter(expiryTime); }
    }

    public static String generateTemporaryPassword(String login) {
        // Generowanie bezpiecznego hasła tymczasowego
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(12);

        for (int i = 0; i < 12; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        String tempPassword = sb.toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(EXPIRY_MINUTES);

        // Zapisanie hasła tymczasowego
        temporaryPasswords.put(login, new TemporaryPassword(tempPassword, expiryTime));

        // Zaplanowanie usunięcia hasła po wygaśnięciu
        scheduler.schedule(() -> {
            temporaryPasswords.remove(login);
            System.out.println("Usunięto wygasłe hasło tymczasowe dla: " + login);
        }, EXPIRY_MINUTES, TimeUnit.MINUTES);

        return tempPassword;
    }

    public static boolean validateTemporaryPassword(String login, String password) {
        TemporaryPassword tempPass = temporaryPasswords.get(login);

        if (tempPass == null) {
            return false;
        }

        if (tempPass.isExpired()) {
            temporaryPasswords.remove(login);
            return false;
        }

        return tempPass.getPassword().equals(password);
    }

    public static void removeTemporaryPassword(String login) {
        temporaryPasswords.remove(login);
    }

}
