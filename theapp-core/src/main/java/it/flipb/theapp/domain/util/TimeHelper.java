package it.flipb.theapp.domain.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class TimeHelper {
    private TimeHelper() {
        // static use only
    }

    public static long toLong(final LocalDateTime _localDateTime) {
        return _localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
    }

    public static LocalDateTime toLocalDateTime(final long _timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(_timestamp), ZoneId.systemDefault());
    }
}
