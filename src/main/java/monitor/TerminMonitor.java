package monitor;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TerminMonitor {
    private static final String LINK = "https://tempus-termine.com/termine/index.php?anr=107&anwendung=2&schlangen=6&sna=Ta60b82812195da3c8bfe8c5b56c475dd&action=open&page=tagesauswahl&tasks=6466&kuerzel=AEHumanita&standortrowid=318&standortanzahl=";
    private static final String FILTER = "<td class=\"monatevent\">";
    private static final MonthDay FROM = MonthDay.of(Month.APRIL, 24);
    private static final MonthDay TO = MonthDay.of(Month.MAY, 2);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int PING_PERIOD = 55;

    private static Properties personalData;
    private static String peopleCount;

    public static void main(String[] args) {
        peopleCount = "For " + args[0] + " people: ";
        System.out.println(peopleCount);

        readProperties();

        while (true) {
            try {
                URL url = new URL(LINK + args[0]);
                String content = new Scanner(url.openStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
                List<Termin> termins = getTermins(content);
                System.out.println(LocalDateTime.now().format(FORMATTER) + " : " + processTermins(termins));

                TimeUnit.SECONDS.sleep(PING_PERIOD);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    private static List<Termin> getTermins(String content) {
        return Arrays.stream(content.split("\n"))
                .filter(string -> string.contains(FILTER))
                .map(s -> {
                    Termin termin = new Termin(s);
                    System.out.println(termin);
                    return termin;
                })
                .filter(termin -> termin.isBetween(FROM, TO))
                .collect(Collectors.toList());
    }

    private static void readProperties() {
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

    private static String processTermins(List<Termin> termins) {
        if (!termins.isEmpty()) {
            sendEmail(peopleCount + termins);
            return termins.toString();
        } else {
            return "nothing";
        }
    }

    private static void sendEmail(String text) {
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
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        personalData.getProperty("gmail.user.name"),
                        personalData.getProperty("gmail.user.password"));
            }
        });
    }
}
