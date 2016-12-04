package it.flipb.theapp.domain.service.scanner.impl;

import it.flipb.theapp.domain.model.plugin.CorePlugin;
import it.flipb.theapp.domain.model.scanner.*;
import it.flipb.theapp.domain.service.scanner.PortScannerService;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.scanner.ScannerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.Null;

@Service
class PortScannerServiceImpl implements PortScannerService {
    private static final Logger logger = LoggerFactory.getLogger(PortScannerServiceImpl.class);

    private final CorePlugin<ScannerPlugin> scannerCorePlugin;
    private final CorePlugin<ExecutorPlugin> executorCorePlugin;

    @Autowired
    PortScannerServiceImpl(@Qualifier("scannerCorePlugin") final CorePlugin<ScannerPlugin> _scannerPluginWrapper,
                           @Qualifier("executorCorePlugin") final CorePlugin<ExecutorPlugin> _executorPluginWrapper) {
        scannerCorePlugin = _scannerPluginWrapper;
        executorCorePlugin = _executorPluginWrapper;
    }

    @Override
    @Null
    public PortScannerResult scan(final PortScannerJob _request) {
        Assert.notNull(_request, "Request cannot be null");
        Assert.isTrue(scannerCorePlugin.isInitialized(), "Scanner plugin must be initialized");
        Assert.isTrue(executorCorePlugin.isInitialized(), "Executor plugin must be initialized");

        logger.info("Performing port scan against: " + _request.getDescription());

        return scannerCorePlugin.get().scan(_request, executorCorePlugin.get());
    }
}
