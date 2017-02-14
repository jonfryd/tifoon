package it.flipb.theapp.application.config;
        
import it.flipb.theapp.domain.model.masterplan.MasterPlan;
import it.flipb.theapp.domain.model.network.Network;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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