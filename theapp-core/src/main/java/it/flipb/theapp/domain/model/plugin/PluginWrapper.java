package it.flipb.theapp.domain.model.plugin;

import org.springframework.plugin.core.Plugin;

import javax.validation.constraints.Null;
import java.util.Optional;

public class PluginWrapper<T extends Plugin<?>> {
    private final Optional<T> t;

    public PluginWrapper(@Null final T _t) {
        t = Optional.ofNullable(_t);
    }

    public Optional<T> getOptional() {
        return t;
    }
}
