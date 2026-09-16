package common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DataUtil {
    public static String today() {
        return LocalDate.now().toString();
    }

    public static String timestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static double toDouble(String value, double fallback) {
        try { return Double.parseDouble(value.trim()); }
        catch (Exception e) { return fallback; }
    }

    public static int toInt(String value, int fallback) {
        try { return Integer.parseInt(value.trim()); }
        catch (Exception e) { return fallback; }
    }
}
