package monitor;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Termin {

    private static final DateTimeFormatter TERMIN_FORMATTER = DateTimeFormatter.ofPattern("dd MMMM", Locale.GERMAN);

    private final MonthDay monthDay;

    public Termin(String s) {
        String s1 = cut(s, "colspan=\"7\">");
        String month = s1.substring(0, s1.indexOf(" "));

        String s2 = cut(cut(s, "<td class=\"monatevent\">"), ">");
        String day = s2.substring(0, s2.indexOf("<"));

        this.monthDay = MonthDay.parse(day + " " + month, TERMIN_FORMATTER);
    }

    public MonthDay getMonthDay() {
        return monthDay;
    }

    public boolean isBetween(MonthDay from, MonthDay to) {
        return monthDay.isAfter(from) && monthDay.isBefore(to);
    }

    private static String cut(String s, String prefix) {
        return s.substring(s.indexOf(prefix) + prefix.length());
    }

    @Override
    public String toString() {
        return monthDay.format(TERMIN_FORMATTER);
    }
}
