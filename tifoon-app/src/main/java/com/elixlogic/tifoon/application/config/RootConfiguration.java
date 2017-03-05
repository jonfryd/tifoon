package com.elixlogic.tifoon.application.config;
        
import com.elixlogic.tifoon.domain.model.masterplan.MasterPlan;
import com.elixlogic.tifoon.domain.model.network.Network;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MasterPlan.class, Network.class})
public class RootConfiguration {
    @Getter
    private final MasterPlan masterPlan;
    @Getter
    private final Network network;

    @Autowired
    public RootConfiguration(final MasterPlan _masterPlan, final Network _network) {
        _masterPlan.validate();
        _network.validate();

        masterPlan = _masterPlan;
        network = _network;
    }
}