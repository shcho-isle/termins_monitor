package monitor;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.util.Objects.isNull;

public class Termin {

    private static final DateTimeFormatter TERMIN_FORMATTER = DateTimeFormatter.ofPattern("d MMMM", Locale.GERMAN);

    private final MonthDay monthDay;

    public Termin(String day, String month) {
        this.monthDay = MonthDay.parse(day + " " + month, TERMIN_FORMATTER);
    }

    public boolean isBetween(MonthDay from, MonthDay to) {
        return (isNull(from) || monthDay.isAfter(from)) && (isNull(to) || monthDay.isBefore(to));
    }

    @Override
    public String toString() {
        return monthDay.format(TERMIN_FORMATTER);
    }
}
