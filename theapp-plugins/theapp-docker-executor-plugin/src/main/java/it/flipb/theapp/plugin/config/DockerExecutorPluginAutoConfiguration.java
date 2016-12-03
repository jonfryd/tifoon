package it.flipb.theapp.plugin.config;

import it.flipb.theapp.plugin.DockerExecutorPlugin;
import it.flipb.theapp.domain.model.docker.DockerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DockerConfiguration.class)
public class DockerExecutorPluginAutoConfiguration {
    @Autowired
    private DockerConfiguration dockerConfiguration;

    @Bean
    DockerExecutorPlugin dockerExecutorPlugin() {
        return new DockerExecutorPlugin(dockerConfiguration);
    }

    public DockerConfiguration getDockerConfiguration() {
        return dockerConfiguration;
    }
}
