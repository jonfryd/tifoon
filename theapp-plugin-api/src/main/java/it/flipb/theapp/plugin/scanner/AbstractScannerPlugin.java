package it.flipb.theapp.plugin.scanner;

import it.flipb.theapp.plugin.DefaultMetadataProvider;
import org.springframework.plugin.metadata.PluginMetadata;

public abstract class AbstractScannerPlugin implements ScannerPlugin {
    @Override
    public PluginMetadata getMetadata() {
        return new DefaultMetadataProvider(getClass().getName()).getMetadata();
    }
}
