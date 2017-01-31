package it.flipb.theapp.domain.model.plugin;

import lombok.Getter;
import lombok.NonNull;
import org.springframework.plugin.core.Plugin;

import java.util.Optional;

public class CorePlugin<T extends Plugin<?>> {
    @NonNull
    @Getter
    private final String supports;
    private final Optional<T> t;

    public CorePlugin(@NonNull final String _supports,
                      final T _t) {
        supports = _supports;
        t = Optional.ofNullable(_t);
    }

    public boolean isInitialized() {
        return t.isPresent();
    }

    public T get() {
        return t.orElse(null);
    }
}
