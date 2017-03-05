package com.elixlogic.tifoon.application.schedulers.impl;

import com.elixlogic.tifoon.application.config.RootConfiguration;
import com.elixlogic.tifoon.application.schedulers.PortScanScheduler;
import com.elixlogic.tifoon.domain.mapper.DtoMapper;
import com.elixlogic.tifoon.domain.model.plugin.CorePlugin;
import com.elixlogic.tifoon.domain.model.scanner.*;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerResultDiffService;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerService;
import com.elixlogic.tifoon.infrastructure.config.PluginConfiguration;
import com.elixlogic.tifoon.plugin.io.IoPlugin;
import com.elixlogic.tifoon.plugin.io.MapProperty;
import com.google.common.io.Files;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@SuppressFBWarnings(value = "OBL_UNSATISFIED_OBLIGATION", justification = "https://github.com/findbugsproject/findbugs/issues/98")
public class PortScanSchedulerImpl implements PortScanScheduler {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Value("${tifoon.onlySaveReportOnChange}")
    private boolean onlySaveReportOnChange;

    @Value("${tifoon.useInitialScanAsBaseline}")
    private boolean useInitialScanAsBaseline;

    @Value("${tifoon.dynamicBaselineMode}")
    private boolean dynamicBaselineMode;

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

            // TODO:
            // - move masterplan config to application.yml
            // - report diff (stats and details)
            // - documentation

            if (!onlySaveReportOnChange || (first && useInitialScanAsBaseline)) {
                log.info("Saving report.");
                saveResults(portScannerResult, portScannerDiff);
            } else if (!portScannerDiff.isUnchanged()) {
                log.warn("One or more changes DETECTED! Saving report.");
                saveResults(portScannerResult, portScannerDiff);

                if (dynamicBaselineMode) {
                    baselinePortScannerResult = portScannerResult;
                    log.info("Dynamic baseline is enabled; baseline changed.");
                }
            } else {
                log.info("No changes detected.");
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

            final String extension = Files.getFileExtension(file.getPath());
            final IoPlugin ioPluginForExtension = pluginConfiguration.getIoPluginByExtension(extension);

            if (ioPluginForExtension != null) {
                // extra mapping meta-data required by YAML plugin, ignored by JSON plugin
                // (Jackson is much smarter with regard to inferring types it seems)
                final MapProperty openHostsMapProperty = new MapProperty(NetworkResult.class, "openHosts", String.class, OpenHost.class);
                final MapProperty openPortsMapProperty = new MapProperty(OpenHost.class, "openPorts", Integer.class, Port.class);
                final PortScannerResult portScannerResult = ioPluginForExtension.load(fis, PortScannerResult.class, Collections.emptyList(), Arrays.asList(openHostsMapProperty, openPortsMapProperty));

                if (portScannerResult != null) {
                    log.info("Baseline port scan result loaded.");

                    return portScannerResult;
                } else {
                    log.warn("Unable to deserialize scan result.");
                }
            } else {
                log.warn("Unable to find registered I/O plugin for extension: " + extension);
            }
        } catch (IOException _e) {
            log.warn("Failed to load baseline port scan result", _e);
        }

        log.info("Using initial result as baseline.");

        return _initialPortScannerResult;
    }

    private void saveResults(final PortScannerResult _portScannerResult,
                             final PortScannerDiff _portScannerDiff) {
        final LocalDateTime localDateTimeBeganAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(_portScannerResult.getBeganAt()), ZoneId.systemDefault());
        final String formattedBeganAt = localDateTimeBeganAt.format(DATE_TIME_FORMATTER);

        final File portScannerResultFile = new File("scans/port_scanner_report_" + formattedBeganAt + "." + ioCorePlugin.getExtension().getDefaultFileExtension());

        saveObject(portScannerResultFile, _portScannerResult);

        // only save diff when there are changes to report
        if (!_portScannerDiff.isUnchanged()) {
            final LocalDateTime baselineLocalDateTimeBeganAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(baselinePortScannerResult.getBeganAt()), ZoneId.systemDefault());
            final String baselineFormattedBeganAt = baselineLocalDateTimeBeganAt.format(DATE_TIME_FORMATTER);

            final File portScannerDiffFile = new File("scans/port_scanner_report_" + baselineFormattedBeganAt + "_diff_" + formattedBeganAt + "." + ioCorePlugin.getExtension().getDefaultFileExtension());

            saveObject(portScannerDiffFile, _portScannerDiff);
        }
    }

    private void saveObject(final File _portScannerResultFile, final Object _object) {
        try {
            FileUtils.forceMkdirParent(_portScannerResultFile);
            final boolean success = _portScannerResultFile.createNewFile();

            if (!success) {
                log.debug("Output file already exists: {}", _portScannerResultFile.getPath());
            }

            @Cleanup final FileOutputStream fos = new FileOutputStream(_portScannerResultFile);

            log.info("Saving file: " + _portScannerResultFile.getPath());

            ioCorePlugin.getExtension().save(fos, _object);

            log.info("Port scan result saved.");
        } catch (IOException _e) {
            log.error("Failed to save port scan result", _e);
        }
    }
}
