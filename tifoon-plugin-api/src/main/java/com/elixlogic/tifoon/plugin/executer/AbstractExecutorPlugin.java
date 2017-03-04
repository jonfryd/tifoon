package com.elixlogic.tifoon.plugin.executer;

import com.elixlogic.tifoon.plugin.DefaultMetadataProvider;
import org.springframework.plugin.metadata.PluginMetadata;

public abstract class AbstractExecutorPlugin implements ExecutorPlugin {
    @Override
    public PluginMetadata getMetadata() {
        return new DefaultMetadataProvider(getClass().getName()).getMetadata();
    }
}
