package it.flipb.theapp.config;
        
import it.flipb.theapp.config.properties.Application;
import it.flipb.theapp.config.properties.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
        
@org.springframework.context.annotation.Configuration
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