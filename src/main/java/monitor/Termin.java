package monitor;

import org.apache.commons.text.StringEscapeUtils;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Termin {

    private static final DateTimeFormatter TERMIN_FORMATTER = DateTimeFormatter.ofPattern("d MMMM", Locale.GERMAN);

    private final MonthDay monthDay;

    public static List<Termin> parse(String s) {
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

    public Termin(String day, String month) {
        this.monthDay = MonthDay.parse(day + " " + month, TERMIN_FORMATTER);
    }

    public boolean isBetween(MonthDay from, MonthDay to) {
        return monthDay.isAfter(from) && monthDay.isBefore(to);
    }

    private static String cutPrefix(String s, String prefix) {
        return s.substring(s.indexOf(prefix) + prefix.length());
    }

    private static String cutSuffix(String s, String suffix) {
        return s.substring(0, s.indexOf(suffix));
    }

    @Override
    public String toString() {
        return monthDay.format(TERMIN_FORMATTER);
    }
}
