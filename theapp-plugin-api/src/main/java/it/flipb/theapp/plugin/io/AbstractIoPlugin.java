package it.flipb.theapp.plugin.io;

import it.flipb.theapp.plugin.DefaultMetadataProvider;
import org.springframework.plugin.metadata.PluginMetadata;

import java.io.InputStream;

public abstract class AbstractIoPlugin implements IoPlugin {
    @Override
    public PluginMetadata getMetadata() {
        return new DefaultMetadataProvider(getClass().getName()).getMetadata();
    }
}
