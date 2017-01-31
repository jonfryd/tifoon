package it.flipb.theapp.plugin.scanner;

import it.flipb.theapp.plugin.DefaultMetadataProvider;
import lombok.NonNull;
import org.springframework.plugin.metadata.PluginMetadata;

public abstract class AbstractScannerPlugin implements ScannerPlugin {
    @Override
    @NonNull
    public PluginMetadata getMetadata() {
        return new DefaultMetadataProvider(getClass().getName()).getMetadata();
    }
}
