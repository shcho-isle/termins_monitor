package monitor;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Utils {

    private static final Properties personalData;

    static {
        String personalDataPath = TerminMonitor.class.getClassLoader().getResource("hidden").getPath()
                + "/personal.properties";

        personalData = new Properties();
        try {
            personalData.load(new FileInputStream(personalDataPath));
        } catch (IOException e) {
            System.err.println("Cannot load personal data.");
            throw new RuntimeException(e);
        }
    }

    public static String cutPrefix(String s, String prefix) {
        return s.substring(s.indexOf(prefix) + prefix.length());
    }

    public static String cutSuffix(String s, String suffix) {
        return s.substring(0, s.indexOf(suffix));
    }

    public static void sendEmail(String text) {
        Session session = getSession();
        try {
            MimeMessage message = getMimeMessage(text, session);
            System.out.print("sending... ");
            Transport.send(message);
            System.out.println("Sent message successfully....");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private static MimeMessage getMimeMessage(String text, Session session) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(personalData.getProperty("email.from")));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(personalData.getProperty("email.recipient1")));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(personalData.getProperty("email.recipient2")));
        message.setSubject("termin date");
        message.setText(text);
        return message;
    }

    private static Session getSession() {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        personalData.getProperty("gmail.user.name"),
                        personalData.getProperty("gmail.user.password"));
            }
        });
    }
}
