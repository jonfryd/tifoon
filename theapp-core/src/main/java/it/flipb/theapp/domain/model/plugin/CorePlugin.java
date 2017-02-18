package it.flipb.theapp.domain.model.plugin;

import lombok.NonNull;
import lombok.Value;
import org.springframework.plugin.core.Plugin;

import javax.annotation.Nullable;

@Value
public class CorePlugin<T extends Plugin<?>> {
    @NonNull
    private final String supports;
    @Nullable
    private final T extension;

    public boolean isInitialized() {
        return extension != null;
    }
}
