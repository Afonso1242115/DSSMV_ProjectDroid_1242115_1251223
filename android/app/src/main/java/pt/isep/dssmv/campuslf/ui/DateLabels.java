package pt.isep.dssmv.campuslf.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

// formata a data para o ecrã sem alterar o valor enviado ao servidor
final class DateLabels {
    private DateLabels() { }

    static String format(String value) {
        if (value == null || value.isEmpty()) return "";
        try {
            String day = value.length() > 10 ? value.substring(0, 10) : value;
            return LocalDate.parse(day).format(DateTimeFormatter.ofPattern("d MMM yyyy",
                    Locale.getDefault()));
        } catch (DateTimeParseException error) {
            return value;
        }
    }
}
