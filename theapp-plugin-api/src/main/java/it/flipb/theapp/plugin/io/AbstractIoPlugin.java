package it.flipb.theapp.plugin.io;

import it.flipb.theapp.plugin.DefaultMetadataProvider;
import lombok.NonNull;
import org.springframework.plugin.metadata.PluginMetadata;

import java.io.InputStream;

public abstract class AbstractIoPlugin implements IoPlugin {
    @Override
    @NonNull
    public PluginMetadata getMetadata() {
        return new DefaultMetadataProvider(getClass().getName()).getMetadata();
    }
}
