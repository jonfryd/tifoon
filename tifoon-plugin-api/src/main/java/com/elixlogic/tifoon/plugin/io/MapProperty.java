package com.elixlogic.tifoon.plugin.io;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class MapProperty {
    private final Class<? extends Object> targetClazz;
    private final String property;
    private final Class<? extends Object> key;
    private final Class<? extends Object> value;
}
