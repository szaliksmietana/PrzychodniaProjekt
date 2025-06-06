package pl.example.przychodniafx;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoginAttemptManager {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final ConcurrentHashMap<String, LoginAttempt> loginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 3;
    private static final int BLOCK_DURATION_MINUTES = 5;

    public static class LoginAttempt {
        private int attempts;
        private LocalDateTime blockUntil;

        public LoginAttempt() {
            this.attempts = 0;
            this.blockUntil = null;
        }

        public int getAttempts() { return attempts; }
        public void incrementAttempts() { this.attempts++; }
        public void resetAttempts() { this.attempts = 0; }

        public LocalDateTime getBlockUntil() { return blockUntil; }
        public void setBlockUntil(LocalDateTime blockUntil) { this.blockUntil = blockUntil; }

        public boolean isBlocked() {
            return blockUntil != null && LocalDateTime.now().isBefore(blockUntil);
        }

        public long getBlockTimeRemaining() {
            if (!isBlocked()) return 0;
            return java.time.Duration.between(LocalDateTime.now(), blockUntil).toMinutes();
        }
    }

    public static boolean isLoginBlocked(String login) {
        LoginAttempt attempt = loginAttempts.get(login);
        if (attempt == null) return false;

        if (attempt.isBlocked()) {
            return true;
        } else if (attempt.getBlockUntil() != null && !attempt.isBlocked()) {
            // Blokada wygasła, resetujemy próby
            attempt.resetAttempts();
            attempt.setBlockUntil(null);
        }

        return false;
    }

    public static void recordFailedAttempt(String login) {
        LoginAttempt attempt = loginAttempts.computeIfAbsent(login, k -> new LoginAttempt());
        attempt.incrementAttempts();

        if (attempt.getAttempts() >= MAX_ATTEMPTS) {
            LocalDateTime blockUntil = LocalDateTime.now().plusMinutes(BLOCK_DURATION_MINUTES);
            attempt.setBlockUntil(blockUntil);

            System.out.println("Zablokowano login: " + login + " do: " + blockUntil);

            // Zaplanowanie odblokowania
            scheduler.schedule(() -> {
                LoginAttempt blockedAttempt = loginAttempts.get(login);
                if (blockedAttempt != null) {
                    blockedAttempt.resetAttempts();
                    blockedAttempt.setBlockUntil(null);
                    System.out.println("Odblokowano login: " + login);
                }
            }, BLOCK_DURATION_MINUTES, TimeUnit.MINUTES);
        }
    }

    public static void recordSuccessfulLogin(String login) {
        LoginAttempt attempt = loginAttempts.get(login);
        if (attempt != null) {
            attempt.resetAttempts();
            attempt.setBlockUntil(null);
        }
    }

    public static int getRemainingAttempts(String login) {
        LoginAttempt attempt = loginAttempts.get(login);
        if (attempt == null) return MAX_ATTEMPTS;
        return Math.max(0, MAX_ATTEMPTS - attempt.getAttempts());
    }

    public static long getBlockTimeRemaining(String login) {
        LoginAttempt attempt = loginAttempts.get(login);
        if (attempt == null) return 0;
        return attempt.getBlockTimeRemaining();
    }
}
