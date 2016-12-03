package it.flipb.theapp.domain.model.plugin;

import org.springframework.plugin.core.Plugin;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Optional;

public class PluginWrapper<T extends Plugin<?>> {
    private final String pluginName;
    private final Optional<T> t;

    public PluginWrapper(@NotNull final String _pluginName,
                         @Null final T _t) {
        pluginName = _pluginName;
        t = Optional.ofNullable(_t);
    }

    public boolean isInitialized() {
        return getOptional().isPresent();
    }

    public Optional<T> getOptional() {
        return t;
    }

    public String getPluginName() {
        return pluginName;
    }
}
