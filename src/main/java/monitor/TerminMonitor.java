package monitor;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
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

    private static int peopleCount;

    public static void main(String[] args) throws InterruptedException {
        peopleCount = Integer.parseInt(args[0]);
        System.out.println("For " + peopleCount + " people: ");

        while (true) {
            pingAvaliableTermins();
            TimeUnit.SECONDS.sleep(PING_PERIOD);
        }
    }

    private static void pingAvaliableTermins() {
        try {
            URL url = new URL(LINK + peopleCount);
            String content = new Scanner(url.openStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
            List<Termin> termins = parse(content);
            System.out.println(LocalDateTime.now().format(FORMATTER) + " : " + processTermins(termins));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Termin> parse(String content) {
        return Arrays.stream(content.split("\n"))
                .filter(string -> string.contains(FILTER))
                .flatMap(s -> {
                    List<Termin> termins = parse(s);
                    System.out.println(termins);
                    return termins.stream();
                })
                .filter(termin -> termin.isBetween(FROM, TO))
                .collect(Collectors.toList());
    }

    private static String processTermins(List<Termin> termins) {
        if (!termins.isEmpty()) {
            Utils.sendEmail("For " + peopleCount + " people: " + termins);
            return termins.toString();
        } else {
            return "nothing";
        }
    }
}
