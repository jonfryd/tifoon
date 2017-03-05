package com.elixlogic.tifoon.plugin.io;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ListProperty {
    private final Class<? extends Object> targetClazz;
    private final String property;
    private final Class<? extends Object> type;
}
