package com.elixlogic.tifoon.domain.model.network;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import java.util.List;

@ConfigurationProperties(locations = {"classpath:network.yml", "classpath:config/network.yml", "file:network.yml", "file:config/network.yml"}, prefix = "network")
@Data
@NoArgsConstructor
public class Network implements Validator {
    private List<Target> targets;

    @Override
    public void validate() {
        Assert.notEmpty(targets, "targets must not be empty");

        targets.forEach(Target::validate);
    }
}