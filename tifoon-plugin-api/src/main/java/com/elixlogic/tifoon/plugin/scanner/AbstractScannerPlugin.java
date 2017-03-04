package com.elixlogic.tifoon.plugin.scanner;

import com.elixlogic.tifoon.plugin.DefaultMetadataProvider;
import org.springframework.plugin.metadata.PluginMetadata;

public abstract class AbstractScannerPlugin implements ScannerPlugin {
    @Override
    public PluginMetadata getMetadata() {
        return new DefaultMetadataProvider(getClass().getName()).getMetadata();
    }
}
