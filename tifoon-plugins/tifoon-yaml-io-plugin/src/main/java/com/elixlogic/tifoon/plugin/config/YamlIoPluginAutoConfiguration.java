package com.elixlogic.tifoon.plugin.config;

import com.elixlogic.tifoon.plugin.YamlIoPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YamlIoPluginAutoConfiguration {
    @Bean
    YamlIoPlugin yamlIoPlugin() {
        return new YamlIoPlugin();
    }
}
