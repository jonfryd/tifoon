package it.flipb.theapp.infrastructure.config;

import it.flipb.theapp.infrastructure.config.properties.tools.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(Tools.class)
public class ToolsConfiguration {
    @Autowired
    private Tools tools;

    public Tools getTools() {
        return tools;
    }
}
