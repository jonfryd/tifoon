package com.elixlogic.tifoon.domain.service.scanner.impl;

import com.elixlogic.tifoon.domain.model.scanner.NetworkResult;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerService;
import com.elixlogic.tifoon.plugin.executer.ExecutorPlugin;
import com.elixlogic.tifoon.plugin.scanner.ScannerPlugin;
import com.elixlogic.tifoon.domain.model.plugin.CorePlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PortScannerServiceImpl implements PortScannerService {
    private final CorePlugin<ScannerPlugin> scannerCorePlugin;
    private final CorePlugin<ExecutorPlugin> executorCorePlugin;

    @Autowired
    public PortScannerServiceImpl(@Qualifier("scannerCorePlugin") final CorePlugin<ScannerPlugin> _scannerCorePlugin,
                                  @Qualifier("executorCorePlugin") final CorePlugin<ExecutorPlugin> _executorCorePlugin) {
        scannerCorePlugin = _scannerCorePlugin;
        executorCorePlugin = _executorCorePlugin;
    }

    private NetworkResult scanNetwork(@NonNull final PortScannerJob _request, @Nullable final String _additionalParameters) {
        Assert.isTrue(scannerCorePlugin.isInitialized(), "Scanner plugin must be initialized");
        Assert.isTrue(executorCorePlugin.isInitialized(), "Executor plugin must be initialized");

        log.info("Performing port scan against: " + _request.getNetworkId());

        return scannerCorePlugin.getExtension().scan(_request, executorCorePlugin.getExtension(), _additionalParameters);
    }

    @Override
    public PortScannerResult scan(@NonNull final List<PortScannerJob> _request,
                                  @Nullable final String _additionalParameters) {
        final long start = System.currentTimeMillis();

        final List<NetworkResult> networkResults = _request
                .parallelStream()
                .map(nr -> scanNetwork(nr, _additionalParameters))
                .collect(Collectors.toList());

        final PortScannerResult portScannerResult = PortScannerResult.builder()
                .beganAt(start)
                .endedAt(System.currentTimeMillis())
                .portScannerJobs(_request)
                .networkResults(networkResults)
                .build();
        portScannerResult.update(); // update jobsHash and status

        return portScannerResult;
    }
}
