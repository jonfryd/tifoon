package com.elixlogic.tifoon.infrastructure.config;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import com.elixlogic.tifoon.plugin.io.IoPlugin;
import com.elixlogic.tifoon.domain.model.core.CoreSettings;
import com.elixlogic.tifoon.domain.model.plugin.CorePlugin;
import com.elixlogic.tifoon.plugin.executer.ExecutorPlugin;
import com.elixlogic.tifoon.plugin.scanner.ScannerPlugin;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnablePluginRegistries({ExecutorPlugin.class, ScannerPlugin.class, IoPlugin.class})
@Slf4j
public class PluginConfiguration implements Validator {
    private final CorePlugin<ScannerPlugin> scannerCorePlugin;
    private final CorePlugin<ExecutorPlugin> executorCorePlugin;
    private final CorePlugin<IoPlugin> saveCorePlugin;
    private final Map<String, IoPlugin> ioPluginsByExtension;

    @Autowired
    public PluginConfiguration(final CoreSettings _coreSettings,
                               @Qualifier("scannerPluginRegistry") final PluginRegistry<ScannerPlugin, String> _scannerPluginRegistry,
                               @Qualifier("executorPluginRegistry") final PluginRegistry<ExecutorPlugin, String> _executorPluginRegistry,
                               @Qualifier("ioPluginRegistry") final PluginRegistry<IoPlugin, String> _ioPluginRegistry) {
        // plugins are wrapped in CorePlugin objects to prevent circular Spring dependencies caused by adding Plugin beans here
        log.debug("Scanner plugins found: " + _scannerPluginRegistry.getPlugins());

        final String scannerSupports = _coreSettings.getScanner().getToolName();
        scannerCorePlugin = new CorePlugin<>(scannerSupports, _scannerPluginRegistry.getPluginFor(scannerSupports));

        log.debug("Executor plugins found: " + _executorPluginRegistry.getPlugins());

        final String executorSupports = _coreSettings.getCommandExecutor();
        executorCorePlugin = new CorePlugin<>(executorSupports, _executorPluginRegistry.getPluginFor(executorSupports));

        log.debug("IO plugins found: " + _ioPluginRegistry.getPlugins());

        ioPluginsByExtension = new HashMap<>();

        // TODO: rewrite to Java 8 stream mapping
        for(final IoPlugin ioPlugin : _ioPluginRegistry.getPlugins()) {
            for(final String extension : ioPlugin.getFileExtensionsHandled()) {
                ioPluginsByExtension.put(extension, ioPlugin);
            }
        }

        final String ioSupports = _coreSettings.getSaveFormat();
        saveCorePlugin = new CorePlugin<>(ioSupports, _ioPluginRegistry.getPluginFor(ioSupports));
    }

    @Override
    public void validate() {
        Assert.isTrue(scannerCorePlugin.isInitialized(),"Scanner plugin which supports '%s' not initialized", scannerCorePlugin.getSupports());
        Assert.isTrue(executorCorePlugin.isInitialized(), "Executor plugin which supports '%s' not initialized", executorCorePlugin.getSupports());
        Assert.isTrue(saveCorePlugin.isInitialized(), "IO plugin which supports '%s' not initialized", saveCorePlugin.getSupports());
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
    public CorePlugin<IoPlugin> saveCorePlugin() {
        return saveCorePlugin;
    }

    @Nullable
    public IoPlugin getIoPluginByExtension(final String _extension) {
        return ioPluginsByExtension.get(_extension);
    }
}
