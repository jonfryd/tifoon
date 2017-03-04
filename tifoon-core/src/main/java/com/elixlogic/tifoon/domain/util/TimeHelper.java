package com.elixlogic.tifoon.domain.util;

import lombok.NonNull;
import lombok.experimental.Helper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public final class TimeHelper {
    public static long toLong(@NonNull final LocalDateTime _localDateTime) {
        return _localDateTime.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
    }

    public static LocalDateTime toLocalDateTime(@NonNull final long _timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(_timestamp), ZoneId.systemDefault());
    }
}
