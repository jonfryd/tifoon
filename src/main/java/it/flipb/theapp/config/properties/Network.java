package it.flipb.theapp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties(prefix="network")
public class Network {
    @NotNull
    @Valid
    private List<Target> targets;

    public List<Target> getTargets() {
        return targets;
    }

    public void setTargets(List<Target> targets) {
        this.targets = targets;
    }

    @Override
    public String toString() {
        return "Network{" +
            "targets=" + targets +
            '}';
    }
}