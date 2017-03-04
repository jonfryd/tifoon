package com.elixlogic.tifoon.plugin.io;

import com.elixlogic.tifoon.plugin.DefaultMetadataProvider;
import org.springframework.plugin.metadata.PluginMetadata;

public abstract class AbstractIoPlugin implements IoPlugin {
    @Override
    public PluginMetadata getMetadata() {
        return new DefaultMetadataProvider(getClass().getName()).getMetadata();
    }
}
