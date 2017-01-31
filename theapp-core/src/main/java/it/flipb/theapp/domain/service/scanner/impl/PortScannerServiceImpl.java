package it.flipb.theapp.domain.service.scanner.impl;

import it.flipb.theapp.domain.model.plugin.CorePlugin;
import it.flipb.theapp.domain.model.scanner.*;
import it.flipb.theapp.domain.service.scanner.PortScannerService;
import it.flipb.theapp.infrastructure.jpa.repositories.PortScannerResultRepository;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.scanner.ScannerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.constraints.Null;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PortScannerServiceImpl implements PortScannerService {
    private static final Logger logger = LoggerFactory.getLogger(PortScannerServiceImpl.class);

    private final CorePlugin<ScannerPlugin> scannerCorePlugin;
    private final CorePlugin<ExecutorPlugin> executorCorePlugin;

    private final PortScannerResultRepository portScannerResultRepository;

    @Autowired
    public PortScannerServiceImpl(@Qualifier("scannerCorePlugin") final CorePlugin<ScannerPlugin> _scannerCorePlugin,
                           @Qualifier("executorCorePlugin") final CorePlugin<ExecutorPlugin> _executorCorePlugin,
                           final PortScannerResultRepository _portScannerResultRepository) {
        scannerCorePlugin = _scannerCorePlugin;
        executorCorePlugin = _executorCorePlugin;
        portScannerResultRepository = _portScannerResultRepository;
    }

    @Null
    private NetworkResult scanNetwork(final PortScannerJob _request) {
        Assert.notNull(_request, "Request cannot be null");
        Assert.isTrue(scannerCorePlugin.isInitialized(), "Scanner plugin must be initialized");
        Assert.isTrue(executorCorePlugin.isInitialized(), "Executor plugin must be initialized");

        logger.info("Performing port scan against: " + _request.getDescription());

        return scannerCorePlugin.get().scan(_request, executorCorePlugin.get());
    }

    @Override
    public PortScannerResult scan(final List<PortScannerJob> _request) {
        final long start = System.currentTimeMillis();

        final NetworkResults networkResults = new NetworkResults(_request
                .parallelStream()
                .map(this::scanNetwork)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));

        // save and reload to populate all ids
        final PortScannerResult portScannerResult = new PortScannerResult(
                start,
                System.currentTimeMillis(),
                true,
                networkResults.getNetworkResults()
        );

        return portScannerResult;
    }

    @Override
    @Transactional
    public PortScannerResult scanAndPersist(final List<PortScannerJob> _request) {
        final PortScannerResult portScannerResult = scan(_request);

        return portScannerResultRepository.save(portScannerResult);
    }
}
