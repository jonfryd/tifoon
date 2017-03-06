package com.elixlogic.tifoon.domain.util;

import lombok.NonNull;
import lombok.experimental.Helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public final class TimeHelper {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static long toLong(@NonNull final LocalDateTime _localDateTime) {
        return _localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
    }

    public static LocalDateTime toLocalDateTime(final long _timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(_timestamp), ZoneId.systemDefault());
    }

    public static String formatTimestamp(final long _timestamp) {
        return toLocalDateTime(_timestamp).format(DATE_TIME_FORMATTER);
    }
}
