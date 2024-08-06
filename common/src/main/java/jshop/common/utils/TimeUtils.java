package jshop.common.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import jshop.common.exception.JshopException;

public class TimeUtils {

    public static Long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            throw new JshopException();
        }
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime timestampToLocalDateTime(Long timestamp) {
        if (timestamp == null) {
            throw new JshopException();
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
}
