package pl.example.przychodniafx.email;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailService {

    private static final String SMTP_HOST = "in-v3.mailjet.com";
    private static final String SMTP_PORT = "587";

    private String emailUsername;
    private String emailPassword;

    public EmailService() {
        loadConfig();
    }

    private void loadConfig() {
        Properties config = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Nie znaleziono pliku!");
            }
            config.load(input);
            emailUsername = config.getProperty("MAILJET_API_KEY");
            emailPassword = config.getProperty("MAILJET_API_SECRET");
            if (emailUsername == null || emailPassword == null) {
                throw new RuntimeException("Brak klucza API lub sekretu w config.properties");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Nie udało się wczytać pliku config.properties");
        }
    }


    public boolean sendTemporaryPassword(String recipientEmail, String temporaryPassword) {
        try {
            Properties props = new Properties();
            props.put("mail.debug", "true");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailUsername, emailPassword);
                }
            });

            Message message = new MimeMessage(session);
            // Adres FROM musi być zweryfikowany w Mailjet, możesz to zmienić na swój
            message.setFrom(new InternetAddress("pomoc_przychodnia@outlook.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Tymczasowe hasło - Przychodnia");

            String emailContent = String.format(
                    "Witaj!\n\n" +
                            "Twoje tymczasowe hasło do logowania: %s\n\n" +
                            "UWAGA: To hasło wygaśnie za 3 minuty!\n" +
                            "Po zalogowaniu się zalecamy zmianę hasła na stałe.\n\n" +
                            "Pozdrawiamy,\n" +
                            "Zespół Przychodni",
                    temporaryPassword
            );

            message.setText(emailContent);

            Transport.send(message);
            System.out.println("Email wysłany.");
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}