package it.flipb.theapp.plugin.executer;

import it.flipb.theapp.plugin.DefaultMetadataProvider;
import lombok.NonNull;
import org.springframework.plugin.metadata.PluginMetadata;

public abstract class AbstractExecutorPlugin implements ExecutorPlugin {
    @Override
    @NonNull
    public PluginMetadata getMetadata() {
        return new DefaultMetadataProvider(getClass().getName()).getMetadata();
    }
}
