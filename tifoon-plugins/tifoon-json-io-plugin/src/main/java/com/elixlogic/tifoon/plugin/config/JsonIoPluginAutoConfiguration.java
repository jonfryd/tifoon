package com.elixlogic.tifoon.plugin.config;

import com.elixlogic.tifoon.plugin.JsonIoPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonIoPluginAutoConfiguration {
    @Bean
    JsonIoPlugin jsonIoPlugin() {
        return new JsonIoPlugin();
    }
}
