package monitor;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Termin {

    private static final DateTimeFormatter TERMIN_FORMATTER = DateTimeFormatter.ofPattern("d MMMM", Locale.GERMAN);

    private final MonthDay monthDay;

    public Termin(String day, String month) {
        this.monthDay = MonthDay.parse(day + " " + month, TERMIN_FORMATTER);
    }

    public boolean isBetween(MonthDay from, MonthDay to) {
        return monthDay.isAfter(from) && monthDay.isBefore(to);
    }

    @Override
    public String toString() {
        return monthDay.format(TERMIN_FORMATTER);
    }
}
