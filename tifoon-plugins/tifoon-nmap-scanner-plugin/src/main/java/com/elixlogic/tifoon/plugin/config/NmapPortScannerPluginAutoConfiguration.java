package com.elixlogic.tifoon.plugin.config;

import com.elixlogic.tifoon.plugin.NmapPortScannerPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NmapPortScannerPluginAutoConfiguration {
    @Bean
    NmapPortScannerPlugin nmapPortScannerPlugin() {
        return new NmapPortScannerPlugin();
    }
}
