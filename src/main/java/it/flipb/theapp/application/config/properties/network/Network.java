package it.flipb.theapp.application.config.properties.network;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties(locations = {"classpath:network.yml", "classpath:config/network.yml", "file:network.yml", "file:config/network.yml"}, prefix = "network")
public class Network {
    @NotNull
    @Valid
    private List<Target> targets;

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(@NotNull List<Target> targets) {
        this.targets = targets;
    }

    @Override
    public String toString() {
        return "Network{" +
            "targets=" + targets +
            '}';
    }
}