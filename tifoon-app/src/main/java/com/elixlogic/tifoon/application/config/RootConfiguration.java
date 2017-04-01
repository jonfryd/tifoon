package com.elixlogic.tifoon.application.config;
        
import com.elixlogic.tifoon.domain.model.core.AppSettings;
import com.elixlogic.tifoon.domain.model.core.CoreSettings;
import com.elixlogic.tifoon.domain.model.network.Network;
import com.elixlogic.tifoon.infrastructure.config.PluginConfiguration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AppSettings.class, CoreSettings.class, Network.class})
public class RootConfiguration {
    @Getter
    private final AppSettings appSettings;
    @Getter
    private final CoreSettings coreSettings;
    @Getter
    private final Network network;
    @Getter
    private final PluginConfiguration pluginConfiguration;

    @Autowired
    public RootConfiguration(final AppSettings _appSettings,
                             final CoreSettings _coreSettings,
                             final Network _network,
                             final PluginConfiguration _pluginConfiguration) {
        _appSettings.validate();
        _coreSettings.validate();
        _network.validate();
        _pluginConfiguration.validate();

        appSettings = _appSettings;
        coreSettings = _coreSettings;
        network = _network;
        pluginConfiguration = _pluginConfiguration;
    }
}