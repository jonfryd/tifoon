package it.flipb.theapp.domain.service.scanner.impl;

import it.flipb.theapp.domain.model.plugin.PluginWrapper;
import it.flipb.theapp.domain.model.scanner.*;
import it.flipb.theapp.domain.service.scanner.PortScannerService;
import it.flipb.theapp.infrastructure.config.PluginConfiguration;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.scanner.ScannerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.Null;
import java.util.Optional;

@Service
class PortScannerServiceImpl implements PortScannerService {
    private static final Logger logger = LoggerFactory.getLogger(PortScannerServiceImpl.class);

    private final Optional<ScannerPlugin> scannerPlugin;
    private final Optional<ExecutorPlugin> executorPlugin;

    @Autowired
    PortScannerServiceImpl(@Qualifier("scannerPluginWrapper") final PluginWrapper<ScannerPlugin> _scannerPluginWrapper,
                           @Qualifier("executorPluginWrapper") final PluginWrapper<ExecutorPlugin> _executorPluginWrapper) {
        scannerPlugin = _scannerPluginWrapper.getOptional();
        executorPlugin = _executorPluginWrapper.getOptional();
    }

    @Override
    @Null
    public PortScannerResult scan(final PortScannerJob _request) {
        Assert.notNull(_request, "Request cannot be null");
        Assert.isTrue(scannerPlugin.isPresent(), "Scanner plugin must be present");
        Assert.isTrue(executorPlugin.isPresent(), "Executor plugin must be present");

        logger.info("Performing port scan against: " + _request.getDescription());

        return scannerPlugin.get().scan(_request, executorPlugin.get());
    }
}
