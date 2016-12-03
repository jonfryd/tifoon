package it.flipb.theapp.domain.service.scanner.impl;

import it.flipb.theapp.domain.model.scanner.*;
import it.flipb.theapp.domain.service.scanner.PortScannerService;
import it.flipb.theapp.infrastructure.config.PluginConfiguration;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.scanner.ScannerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Service
class PortScannerServiceImpl implements PortScannerService {
    private static final Logger logger = LoggerFactory.getLogger(PortScannerServiceImpl.class);

    private final ScannerPlugin scannerPlugin;
    private final ExecutorPlugin executorPlugin;

    @Autowired
    PortScannerServiceImpl(PluginConfiguration _pluginConfiguration) {
        scannerPlugin = _pluginConfiguration.getScannerPlugin();
        executorPlugin = _pluginConfiguration.getExecutorPlugin();
    }

    @Override
    @Null
    public PortScannerResult scan(final PortScannerJob _request) {
        Assert.notNull(scannerPlugin, "Scanner plugin cannot be null");
        Assert.notNull(executorPlugin, "Executor plugin cannot be null");

        logger.info("Performing port scan against: " + _request.getDescription());

        return scannerPlugin.scan(_request, executorPlugin);
    }
}
