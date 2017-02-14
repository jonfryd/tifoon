package it.flipb.theapp.domain.service.scanner.impl;

import it.flipb.theapp.domain.model.plugin.CorePlugin;
import it.flipb.theapp.domain.model.scanner.*;
import it.flipb.theapp.domain.service.scanner.PortScannerService;
import it.flipb.theapp.infrastructure.jpa.repository.PortScannerResultRepository;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import it.flipb.theapp.plugin.scanner.ScannerPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PortScannerServiceImpl implements PortScannerService {
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

    @NonNull
    private NetworkResult scanNetwork(@NonNull final PortScannerJob _request) {
        Assert.isTrue(scannerCorePlugin.isInitialized(), "Scanner plugin must be initialized");
        Assert.isTrue(executorCorePlugin.isInitialized(), "Executor plugin must be initialized");

        log.info("Performing port scan against: " + _request.getNetworkId());

        return scannerCorePlugin.get().scan(_request, executorCorePlugin.get());
    }

    @Override
    @NonNull
    public PortScannerResult scan(@NonNull final List<PortScannerJob> _request) {
        final long start = System.currentTimeMillis();

        final List<NetworkResult> networkResults = _request
                .parallelStream()
                .map(this::scanNetwork)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // save and reload to populate all ids
        final PortScannerResult portScannerResult = new PortScannerResult(
                start,
                System.currentTimeMillis(),
                true,
                networkResults);

        return portScannerResult;
    }

    @Override
    @Transactional
    @NonNull
    public PortScannerResult scanAndPersist(@NonNull final List<PortScannerJob> _request) {
        final PortScannerResult portScannerResult = scan(_request);

        return portScannerResultRepository.save(portScannerResult);
    }
}
