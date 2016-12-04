package it.flipb.theapp.infrastructure.config;

import it.flipb.theapp.domain.model.masterplan.MasterPlan;
import it.flipb.theapp.domain.model.plugin.CorePlugin;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.scanner.ScannerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

@Configuration
@EnablePluginRegistries({ExecutorPlugin.class, ScannerPlugin.class})
public class PluginConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PluginConfiguration.class);

    private final CorePlugin<ScannerPlugin> scannerCorePlugin;
    private final CorePlugin<ExecutorPlugin> executorCorePlugin;

    @Autowired
    public PluginConfiguration(final MasterPlan _masterPlan,
                               @Qualifier("scannerPluginRegistry") final PluginRegistry<ScannerPlugin, String> _scannerPluginRegistry,
                               @Qualifier("executorPluginRegistry") final PluginRegistry<ExecutorPlugin, String> _executorPluginRegistry) {
        // plugins are wrapped in CorePlugin objects to prevent circular Spring dependencies caused by adding Plugin beans here

        logger.debug("Scanner plugins found: " + _scannerPluginRegistry.getPlugins());

        final String scannerSupports = _masterPlan.getScanner().getToolName();
        scannerCorePlugin = new CorePlugin<>(scannerSupports, _scannerPluginRegistry.getPluginFor(scannerSupports));

        logger.debug("Executor plugins found: " + _executorPluginRegistry.getPlugins());

        final String executorSupports = _masterPlan.getCommandExecutor();
        executorCorePlugin = new CorePlugin<>(executorSupports, _executorPluginRegistry.getPluginFor(executorSupports));
    }

    public boolean verify() {
        if (!scannerCorePlugin.isInitialized()) {
            logger.error(String.format("Scanner plugin which supports '%s' not initialized", scannerCorePlugin.getSupports()));
            return false;
        }
        if (!executorCorePlugin.isInitialized()) {
            logger.error(String.format("Executor plugin which supports '%s' not initialized", executorCorePlugin.getSupports()));
            return false;
        }

        return true;
    }

    @Bean
    public CorePlugin<ScannerPlugin> scannerCorePlugin() {
        return scannerCorePlugin;
    }

    @Bean
    public CorePlugin<ExecutorPlugin> executorCorePlugin() {
        return executorCorePlugin;
    }
}
