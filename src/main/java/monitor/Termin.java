package monitor;

import org.apache.commons.text.StringEscapeUtils;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Termin {

    private static final DateTimeFormatter TERMIN_FORMATTER = DateTimeFormatter.ofPattern("d MMMM", Locale.GERMAN);

    private final MonthDay monthDay;

    public Termin(String s) {
        String s1 = cutPrefix(s, "colspan=\"7\">");
        String month = StringEscapeUtils.unescapeHtml4(cutSuffix(s1, " "));

        String s2 = cutPrefix(cutPrefix(s, "<td class=\"monatevent\">"), ">");
        String day = cutSuffix(s2, "<");

        this.monthDay = MonthDay.parse(day + " " + month, TERMIN_FORMATTER);
    }

    public MonthDay getMonthDay() {
        return monthDay;
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
