package it.flipb.theapp.infrastructure.config;

import it.flipb.theapp.domain.model.profile.MasterPlan;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.scanner.ScannerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

import javax.validation.constraints.Null;

@Configuration
@EnablePluginRegistries({ExecutorPlugin.class, ScannerPlugin.class})
public class PluginConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(PluginConfiguration.class);

    private final MasterPlan masterPlan;

    private final ScannerPlugin scannerPlugin;
    private final ExecutorPlugin executorPlugin;

    @Autowired
    public PluginConfiguration(final MasterPlan _masterPlan,
                               @Qualifier("scannerPluginRegistry") final PluginRegistry<ScannerPlugin, String> _scannerPluginRegistry,
                               @Qualifier("executorPluginRegistry") final PluginRegistry<ExecutorPlugin, String> _executorPluginRegistry) {
        masterPlan = _masterPlan;

        logger.debug("Scanner plugins found: " + _scannerPluginRegistry.getPlugins());

        scannerPlugin = _scannerPluginRegistry.getPluginFor(masterPlan.getScanner().getToolName());

        logger.debug("Executor plugins found: " + _executorPluginRegistry.getPlugins());

        executorPlugin = _executorPluginRegistry.getPluginFor(masterPlan.getCommandExecutor());
    }

    public boolean verify() {
        if (scannerPlugin == null) {
            logger.error(String.format("Scanner plugin '%s' not initialized", masterPlan.getScanner().getToolName()));
            return false;
        }
        if (executorPlugin == null) {
            logger.error(String.format("Executor plugin '%s' not initialized", masterPlan.getCommandExecutor()));
            return false;
        }

        return true;
    }

    @Null
    public ScannerPlugin getScannerPlugin() {
        return scannerPlugin;
    }

    @Null
    public ExecutorPlugin getExecutorPlugin()
    {
        return executorPlugin;
    }
}
