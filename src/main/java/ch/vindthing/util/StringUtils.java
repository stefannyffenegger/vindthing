package ch.vindthing.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtils {
    private static final String TIME_FORMATTER = "YYYY-MM-dd:HH:mm:ss";

    public static String getCurrentTimeStamp() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMATTER);
        return LocalDateTime.now().format(formatter);
    }
}