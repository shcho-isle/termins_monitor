package monitor;

import org.apache.commons.text.StringEscapeUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static monitor.Utils.cutPrefix;
import static monitor.Utils.cutSuffix;

public class TerminMonitor {
    private static final String LINK = "https://tempus-termine.com/termine/index.php?anr=107&anwendung=2&schlangen=6&sna=Ta60b82812195da3c8bfe8c5b56c475dd&action=open&page=tagesauswahl&tasks=6466&kuerzel=AEHumanita&standortrowid=318&standortanzahl=";
    private static final String FILTER = "<td class=\"monatevent\">";
    private static final MonthDay FROM = MonthDay.of(Month.APRIL, 24);
    private static final MonthDay TO = null;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int PING_PERIOD = 55;

    private static int peopleCount;

    public static void main(String[] args) throws InterruptedException {
        peopleCount = Integer.parseInt(args[0]);
        System.out.println("For " + peopleCount + " people: ");

        while (true) {
            List<Termin> termins = getTermins();
            String result = processTermins(termins);

            System.out.println(LocalDateTime.now().format(FORMATTER) + " : " + result);

            TimeUnit.SECONDS.sleep(PING_PERIOD);
        }
    }

    private static List<Termin> getTermins() {
        try {
            URL url = new URL(LINK + peopleCount);
            String content = new Scanner(url.openStream(), StandardCharsets.UTF_8).useDelimiter("\\A").next();
            return parseContent(content);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<Termin> parseContent(String content) {
        return Arrays.stream(content.split("\n"))
                .filter(string -> string.contains(FILTER))
                .flatMap(string -> {
                    List<Termin> termins = parseString(string);
                    System.out.println(termins);
                    return termins.stream();
                })
                .filter(termin -> termin.isBetween(FROM, TO))
                .collect(Collectors.toList());
    }

    private static List<Termin> parseString(String s) {
        List<Termin> result = new ArrayList<>();

        String s1 = cutPrefix(s, "colspan=\"7\">");
        String month = StringEscapeUtils.unescapeHtml4(cutSuffix(s1, " "));

        String[] split = s.split("<td class=\"monatevent\">");
        for (int i = 1; i < split.length; i++) {
            String s2 = cutPrefix(split[i], ">");
            String day = cutSuffix(s2, "<");
            result.add(new Termin(day, month));
        }

        return result;
    }

    private static String processTermins(List<Termin> termins) {
        if (termins.isEmpty()) {
            return "termins are missing or filtered";
        }

        Utils.sendEmail("For " + peopleCount + " people: " + termins);
        return termins.toString();
    }
}
