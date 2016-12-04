package it.flipb.theapp.domain.model.plugin;

import org.springframework.plugin.core.Plugin;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Optional;

public class CorePlugin<T extends Plugin<?>> {
    private final String supports;
    private final Optional<T> t;

    public CorePlugin(@NotNull final String _supports,
                      @Null final T _t) {
        supports = _supports;
        t = Optional.ofNullable(_t);
    }

    public boolean isInitialized() {
        return t.isPresent();
    }

    public T get() {
        return t.orElse(null);
    }

    public String getSupports() {
        return supports;
    }
}
