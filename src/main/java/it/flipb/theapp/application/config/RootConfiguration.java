package it.flipb.theapp.application.config;
        
import it.flipb.theapp.application.config.properties.application.Application;
import it.flipb.theapp.application.config.properties.network.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({Application.class, Network.class})
public class RootConfiguration {
    @Autowired
    private Application application;

    @Autowired
    private Network network;

    public Application getApplication() {
        return application;
    }

    public Network getNetwork() {
        return network;
    }
}