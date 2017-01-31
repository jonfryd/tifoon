package it.flipb.theapp.plugin.config;

import it.flipb.theapp.plugin.JsonIoPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsonIoPluginAutoConfiguration {
    @Bean
    JsonIoPlugin jsonIoPlugin() {
        return new JsonIoPlugin();
    }
}
