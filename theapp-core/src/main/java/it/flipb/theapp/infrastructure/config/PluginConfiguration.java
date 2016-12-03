package it.flipb.theapp.infrastructure.config;

import it.flipb.theapp.domain.model.masterplan.MasterPlan;
import it.flipb.theapp.domain.model.plugin.PluginWrapper;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.scanner.ScannerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@EnablePluginRegistries({ExecutorPlugin.class, ScannerPlugin.class})
public class PluginConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PluginConfiguration.class);

    private final PluginWrapper<ScannerPlugin> scannerPluginWrapper;
    private final PluginWrapper<ExecutorPlugin> executorPluginWrapper;

    @Autowired
    public PluginConfiguration(final MasterPlan _masterPlan,
                               @Qualifier("scannerPluginRegistry") final PluginRegistry<ScannerPlugin, String> _scannerPluginRegistry,
                               @Qualifier("executorPluginRegistry") final PluginRegistry<ExecutorPlugin, String> _executorPluginRegistry) {
        // Note that we have to wrap the plugins to prevent circular Spring dependencies
        logger.debug("Scanner plugins found: " + _scannerPluginRegistry.getPlugins());

        final String scannerPluginName = _masterPlan.getScanner().getToolName();
        final ScannerPlugin scannerPlugin = _scannerPluginRegistry.getPluginFor(scannerPluginName);
        scannerPluginWrapper = new PluginWrapper(scannerPluginName, scannerPlugin);

        logger.debug("Executor plugins found: " + _executorPluginRegistry.getPlugins());

        final String executorPluginName = _masterPlan.getCommandExecutor();
        final ExecutorPlugin executorPlugin = _executorPluginRegistry.getPluginFor(executorPluginName);
        executorPluginWrapper = new PluginWrapper(executorPluginName, executorPlugin);
    }

    public boolean verify() {
        if (!scannerPluginWrapper.isInitialized()) {
            logger.error(String.format("Scanner plugin '%s' not initialized", scannerPluginWrapper.getPluginName()));
            return false;
        }
        if (!executorPluginWrapper.isInitialized()) {
            logger.error(String.format("Executor plugin '%s' not initialized", executorPluginWrapper.getPluginName()));
            return false;
        }

        return true;
    }

    @Bean
    public PluginWrapper<ScannerPlugin> scannerPluginWrapper() {
        return scannerPluginWrapper;
    }

    @Bean
    public PluginWrapper<ExecutorPlugin> executorPluginWrapper() {
        return executorPluginWrapper;
    }
}
