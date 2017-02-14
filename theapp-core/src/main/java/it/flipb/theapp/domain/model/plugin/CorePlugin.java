package it.flipb.theapp.domain.model.plugin;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.plugin.core.Plugin;

import javax.annotation.Nullable;

public class CorePlugin<T extends Plugin<?>> {
    @NonNull
    @Getter
    private final String supports;
    @Nullable
    private final T t;

    public CorePlugin(@NonNull final String _supports,
                      @Nullable final T _t) {
        supports = _supports;
        t = _t;
    }

    public boolean isInitialized() {
        return t != null;
    }

    @Nullable
    public T get() {
        return t;
    }
}
