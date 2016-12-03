package it.flipb.theapp.application.config;
        
import it.flipb.theapp.domain.model.masterplan.MasterPlan;
import it.flipb.theapp.domain.model.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({MasterPlan.class, Network.class})
public class RootConfiguration {
    @Autowired
    private MasterPlan masterPlan;

    @Autowired
    private Network network;

    public MasterPlan getMasterPlan() {
        return masterPlan;
    }

    public Network getNetwork() {
        return network;
    }
}