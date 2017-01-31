package it.flipb.theapp.domain.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(locations = {"classpath:network.yml", "classpath:config/network.yml", "file:network.yml", "file:config/network.yml"}, prefix = "network")
@Data
@NoArgsConstructor
public class Network {
    @NonNull
    private List<Target> targets;
}