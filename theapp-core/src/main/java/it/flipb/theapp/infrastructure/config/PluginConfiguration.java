package it.flipb.theapp.infrastructure.config;

import it.flipb.theapp.domain.model.masterplan.MasterPlan;
import it.flipb.theapp.domain.model.plugin.CorePlugin;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.io.IoPlugin;
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
@EnablePluginRegistries({ExecutorPlugin.class, ScannerPlugin.class, IoPlugin.class})
public class PluginConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PluginConfiguration.class);

    private final CorePlugin<ScannerPlugin> scannerCorePlugin;
    private final CorePlugin<ExecutorPlugin> executorCorePlugin;
    private final CorePlugin<IoPlugin> ioCorePlugin;

    @Autowired
    public PluginConfiguration(final MasterPlan _masterPlan,
                               @Qualifier("scannerPluginRegistry") final PluginRegistry<ScannerPlugin, String> _scannerPluginRegistry,
                               @Qualifier("executorPluginRegistry") final PluginRegistry<ExecutorPlugin, String> _executorPluginRegistry,
                               @Qualifier("ioPluginRegistry") final PluginRegistry<IoPlugin, String> _ioPluginRegistry) {
        // plugins are wrapped in CorePlugin objects to prevent circular Spring dependencies caused by adding Plugin beans here

        logger.debug("Scanner plugins found: " + _scannerPluginRegistry.getPlugins());

        final String scannerSupports = _masterPlan.getScanner().getToolName();
        scannerCorePlugin = new CorePlugin<>(scannerSupports, _scannerPluginRegistry.getPluginFor(scannerSupports));

        logger.debug("Executor plugins found: " + _executorPluginRegistry.getPlugins());

        final String executorSupports = _masterPlan.getCommandExecutor();
        executorCorePlugin = new CorePlugin<>(executorSupports, _executorPluginRegistry.getPluginFor(executorSupports));

        logger.debug("IO plugins found: " + _ioPluginRegistry.getPlugins());

        final String ioSupports = _masterPlan.getIoFormat();
        ioCorePlugin = new CorePlugin<>(ioSupports, _ioPluginRegistry.getPluginFor(ioSupports));
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
        if (!ioCorePlugin.isInitialized()) {
            logger.error(String.format("IO plugin which supports '%s' not initialized", ioCorePlugin.getSupports()));
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

    @Bean
    public CorePlugin<IoPlugin> ioCorePlugin() {
        return ioCorePlugin;
    }
}
