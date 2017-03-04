package com.elixlogic.tifoon.plugin.config;

import com.elixlogic.tifoon.plugin.ProcessExecutorPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProcessExecutorPluginAutoConfiguration {
    @Bean
    ProcessExecutorPlugin processExecutorPlugin() {
        return new ProcessExecutorPlugin();
    }
}
