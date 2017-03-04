package com.elixlogic.tifoon.plugin.config;

import com.elixlogic.tifoon.plugin.DockerExecutorPlugin;
import com.elixlogic.tifoon.domain.model.docker.DockerConfiguration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DockerConfiguration.class)
public class DockerExecutorPluginAutoConfiguration {
    @Getter
    private final DockerConfiguration dockerConfiguration;

    @Autowired
    public DockerExecutorPluginAutoConfiguration(final DockerConfiguration _dockerConfiguration) {
        _dockerConfiguration.validate();

        dockerConfiguration = _dockerConfiguration;
    }

    @Bean
    DockerExecutorPlugin dockerExecutorPlugin() {
        return new DockerExecutorPlugin(dockerConfiguration);
    }
}
