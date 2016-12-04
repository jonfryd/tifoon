package it.flipb.theapp.plugin.config;

import it.flipb.theapp.plugin.ProcessExecutorPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessExecutorPluginAutoConfiguration {
    @Bean
    ProcessExecutorPlugin processExecutorPlugin() {
        return new ProcessExecutorPlugin();
    }
}
