package com.shakhawat.rbacabac.util;

import lombok.experimental.UtilityClass;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class DateTimeUtil {

    private static final DateTimeFormatter DEFAULT_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }

    public String formatISO(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }

    public LocalDateTime parse(String dateTimeString) {
        return dateTimeString != null ? LocalDateTime.parse(dateTimeString, DEFAULT_FORMATTER) : null;
    }

    public boolean isBetween(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return target.isAfter(start) && target.isBefore(end);
    }
}
