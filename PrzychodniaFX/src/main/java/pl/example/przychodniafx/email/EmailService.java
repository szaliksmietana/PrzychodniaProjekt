package pl.example.przychodniafx.email;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.*;

import java.util.Properties;

public class EmailService {
    private static final String SMTP_HOST = "smtp-mail.outlook.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "pomoc_przychodnia";
    private static final String EMAIL_PASSWORD = "vissxhhmwovaacks";

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
                    return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME));
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
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}
