package experiments;

import org.apache.commons.text.StringEscapeUtils;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Test {
    private static final DateTimeFormatter TERMIN_FORMATTER = DateTimeFormatter.ofPattern("d MMMM", Locale.GERMAN);
    public static void main(String[] args) {
        System.out.println(MonthDay.parse(StringEscapeUtils.unescapeHtml4("30 M&auml;rz"), TERMIN_FORMATTER).format(TERMIN_FORMATTER));
    }
}
