package it.flipb.theapp.plugin.config;

import it.flipb.theapp.plugin.NmapPortScannerPlugin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NmapPortScannerPluginAutoConfiguration {
    @Bean
    NmapPortScannerPlugin nmapPortScannerPlugin() {
        return new NmapPortScannerPlugin();
    }
}
