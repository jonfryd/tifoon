package com.elixlogic.tifoon.application.schedulers.impl;

import com.elixlogic.tifoon.application.config.RootConfiguration;
import com.elixlogic.tifoon.application.schedulers.PortScanScheduler;
import com.elixlogic.tifoon.domain.mapper.DtoMapper;
import com.elixlogic.tifoon.domain.model.plugin.CorePlugin;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerResultDiffService;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerService;
import com.elixlogic.tifoon.infrastructure.config.PluginConfiguration;
import com.elixlogic.tifoon.plugin.io.IoPlugin;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PortScanSchedulerImpl implements PortScanScheduler {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Value("${tifoon.onlySaveReportOnChange}")
    private boolean onlySaveReportOnChange;

    @Value("${tifoon.useInitialScanAsBaseline}")
    private boolean useInitialScanAsBaseline;

    @Value("${tifoon.baselineFilename}")
    private String baselineFilename;

    private boolean first = true;
    private PortScannerResult baselinePortScannerResult;

    private final RootConfiguration configuration;
    private final DtoMapper dtoMapper;
    private final PortScannerService portScannerService;
    private final PortScannerResultDiffService portScannerResultDiffService;
    private final PluginConfiguration pluginConfiguration;
    private final CorePlugin<IoPlugin> ioCorePlugin;

    @Autowired
    public PortScanSchedulerImpl(final RootConfiguration _configuration,
                                 final DtoMapper _dtoMapper,
                                 final PortScannerService _portScannerService,
                                 final PortScannerResultDiffService _portScannerResultDiffService,
                                 final PluginConfiguration _pluginConfiguration,
                                 @Qualifier("ioCorePlugin") final CorePlugin<IoPlugin> _ioCorePlugin) {
        configuration = _configuration;
        dtoMapper = _dtoMapper;
        portScannerService = _portScannerService;
        portScannerResultDiffService = _portScannerResultDiffService;
        pluginConfiguration = _pluginConfiguration;
        ioCorePlugin = _ioCorePlugin;

        log.debug(_configuration.getMasterPlan().toString());
        log.debug(_configuration.getNetwork().toString());
    }

    @Override
    @Scheduled(fixedRateString = "${tifoon.scanRateSeconds}000")
    @Transactional
    public void performScan() {
        if (configuration.getMasterPlan().getScanner().isActive()) {
            if (!pluginConfiguration.verify()) {
                return;
            }

            log.info("Scanning...");

            final List<PortScannerJob> portScannerJobs = configuration.getNetwork().getTargets()
                    .stream()
                    .map(target -> dtoMapper.map(target, PortScannerJob.class))
                    .collect(Collectors.toList());
            final PortScannerResult portScannerResult = portScannerService.scanAndPersist(portScannerJobs);

            if (first) {
                baselinePortScannerResult = useInitialScanAsBaseline ? 
                        portScannerResult :
                        loadBaselinePortScannerResult(baselineFilename, portScannerResult);
            }

            final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(baselinePortScannerResult, portScannerResult);

            // differs from baseline?

            if (!onlySaveReportOnChange || (first && useInitialScanAsBaseline)) {
                log.info("Saving report.");
                savePortScannerResult(portScannerResult);
            } else if (!portScannerDiff.isUnchanged()) {
                log.info("Change detected! Saving report.");
                savePortScannerResult(portScannerResult);
            }

            first = false;

            log.info("Scanning completed.");
        } else {
            log.info("Scanning not enabled.");
        }
    }

    private PortScannerResult loadBaselinePortScannerResult(final String _baselineFilename, final PortScannerResult _initialPortScannerResult) {
        final File file = new File(_baselineFilename);

        try {
            @Cleanup final FileInputStream fis = new FileInputStream(file);

            log.info("Loading baseline file: " + _baselineFilename);

            final PortScannerResult portScannerResult = ioCorePlugin.getExtension().load(fis, PortScannerResult.class);

            log.info("Baseline port scan result loaded.");

            return portScannerResult;
        } catch (IOException _e) {
            log.warn("failed to load baseline port scan result - reverting to initial scan result", _e);

            return _initialPortScannerResult;
        }
    }

    private void savePortScannerResult(final PortScannerResult _portScannerResult) {
        final LocalDateTime localDateTimeBeganAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(_portScannerResult.getBeganAt()), ZoneId.systemDefault());
        final String formattedBeganAt = localDateTimeBeganAt.format(DATE_TIME_FORMATTER);

        final File file = new File("scans/port_scanner_report_" + formattedBeganAt + "." + ioCorePlugin.getSupports());

        try {
            FileUtils.forceMkdirParent(file);
            final boolean success = file.createNewFile();

            if (!success) {
                log.debug("output file already exists: {}", file.getPath());
            }

            @Cleanup final FileOutputStream fos = new FileOutputStream(file);

            log.info("Saving file: " + file.getPath());

            ioCorePlugin.getExtension().save(fos, _portScannerResult);

            log.info("Port scan result saved.");
        } catch (IOException _e) {
            log.error("failed to save port scan result", _e);
        }
    }
}
