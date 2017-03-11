package com.elixlogic.tifoon.application.schedulers;

import com.elixlogic.tifoon.application.config.RootConfiguration;
import com.elixlogic.tifoon.domain.mapper.DtoMapper;
import com.elixlogic.tifoon.domain.model.network.IanaServiceEntry;
import com.elixlogic.tifoon.domain.model.plugin.CorePlugin;
import com.elixlogic.tifoon.domain.model.scanner.*;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;
import com.elixlogic.tifoon.domain.service.scanner.KnownPortsLookupService;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerResultDiffService;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerService;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerStatsService;
import com.elixlogic.tifoon.domain.util.TimeHelper;
import com.elixlogic.tifoon.infrastructure.config.PluginConfiguration;
import com.elixlogic.tifoon.infrastructure.jpa.repository.PortScannerResultRepository;
import com.elixlogic.tifoon.plugin.io.IoPlugin;
import com.elixlogic.tifoon.plugin.io.MapProperty;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
@SuppressFBWarnings(value = "OBL_UNSATISFIED_OBLIGATION", justification = "https://github.com/findbugsproject/findbugs/issues/98")
public class PortScanScheduler {
    private final RootConfiguration rootConfiguration;
    private final DtoMapper dtoMapper;
    private final PortScannerService portScannerService;
    private final PortScannerResultRepository portScannerResultRepository;
    private final PortScannerResultDiffService portScannerResultDiffService;
    private final PortScannerStatsService portScannerStatsService;
    private final KnownPortsLookupService knownPortsLookupService;
    private final PluginConfiguration pluginConfiguration;
    private final CorePlugin<IoPlugin> saveCorePlugin;

    private boolean firstScan = true;
    @Nullable
    private PortScannerResult baselinePortScannerResult;

    @Autowired
    public PortScanScheduler(final RootConfiguration _rootConfiguration,
                             final DtoMapper _dtoMapper,
                             final PortScannerService _portScannerService,
                             final PortScannerResultRepository _portScannerResultRepository,
                             final PortScannerResultDiffService _portScannerResultDiffService,
                             final PortScannerStatsService _portScannerStatsService,
                             final KnownPortsLookupService _knownPortsLookupService,
                             final PluginConfiguration _pluginConfiguration,
                             @Qualifier("saveCorePlugin") final CorePlugin<IoPlugin> _saveCorePlugin) {
        rootConfiguration = _rootConfiguration;
        dtoMapper = _dtoMapper;
        portScannerService = _portScannerService;
        portScannerResultRepository = _portScannerResultRepository;
        portScannerResultDiffService = _portScannerResultDiffService;
        portScannerStatsService = _portScannerStatsService;
        knownPortsLookupService = _knownPortsLookupService;
        pluginConfiguration = _pluginConfiguration;
        saveCorePlugin = _saveCorePlugin;

        log.debug(_rootConfiguration.getAppSettings().toString());
        log.debug(_rootConfiguration.getCoreSettings().toString());
        log.debug(_rootConfiguration.getNetwork().toString());
    }

    @Scheduled(fixedRateString = "${tifoon.scanRateSeconds}000")
    @Transactional
    public void performScan() {
        if (rootConfiguration.getCoreSettings().getScanner().isActive()) {
            if (!pluginConfiguration.verify()) {
                return;
            }

            log.info("Scanning...");

            final List<PortScannerJob> portScannerJobs = rootConfiguration.getNetwork().getTargets()
                    .stream()
                    .map(target -> dtoMapper.map(target, PortScannerJob.class))
                    .collect(Collectors.toList());

            // scan and save to in-memory H2 DB repository (generates primary IDs)...
            // For now it's complete overkill to use Spring Data JPA for the sole purpose of generating an ID, but
            // down the line my gut feeling is that it is worth the effort, since we can easily persist
            // scans to physical DBMS (MySQL, PostgreSQL, etc). JPA and JaVers are buddies, which is important, too.
            final PortScannerResult portScannerResult = portScannerResultRepository.save(portScannerService.scan(portScannerJobs));

            if (firstScan) {
                baselinePortScannerResult = rootConfiguration.getAppSettings().isUseInitialScanAsBaseline() ?
                        portScannerResult :
                        loadBaselinePortScannerResult(rootConfiguration.getAppSettings().getBaselineFilename(), portScannerResult);
            }

            final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(baselinePortScannerResult, portScannerResult);
            final PortScannerDiffDetails portScannerDiffDetails = portScannerStatsService.createDetails(baselinePortScannerResult, portScannerResult, portScannerDiff);

            if (!rootConfiguration.getAppSettings().isOnlySaveReportOnChange() || (firstScan && rootConfiguration.getAppSettings().isUseInitialScanAsBaseline())) {
                log.info("Saving report.");
                saveResults(portScannerResult, portScannerDiff, portScannerDiffDetails);
            } else if (!portScannerDiff.isUnchanged()) {
                log.warn("One or more changes DETECTED! Saving report.");
                saveResults(portScannerResult, portScannerDiff, portScannerDiffDetails);

                if (rootConfiguration.getAppSettings().isDynamicBaselineMode()) {
                    baselinePortScannerResult = portScannerResult;
                    log.info("Dynamic baseline is enabled; baseline changed.");
                }
            } else {
                log.info("No changes detected.");
            }

            // clean-up step - remove scan from in-memory repository
            portScannerResultRepository.delete(portScannerResult);

            firstScan = false;

            log.info("Scanning completed.");
        } else {
            log.info("Scanning not enabled.");
        }
    }

    private PortScannerResult loadBaselinePortScannerResult(@NonNull final String _baselineFilename,
                                                            @NonNull final PortScannerResult _initialPortScannerResult) {
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

    private void saveResults(@NonNull final PortScannerResult _portScannerResult,
                             @NonNull final PortScannerDiff _portScannerDiff,
                             @NonNull final PortScannerDiffDetails _portScannerDiffDetails) {
        final String formattedBeganAt = TimeHelper.formatTimestamp(_portScannerResult.getBeganAt());

        final File portScannerResultFile = new File("scans/port_scanner_report_" + formattedBeganAt + "." + saveCorePlugin.getExtension().getDefaultFileExtension());

        saveObject(portScannerResultFile, _portScannerResult, Collections.emptyList());

        // only save diff when there are changes to report
        if (!_portScannerDiff.isUnchanged()) {
            logDiffDetails(_portScannerDiffDetails);

            final String baselineFormattedBeganAt = TimeHelper.formatTimestamp(baselinePortScannerResult.getBeganAt());
            final File portScannerDiffFile = new File("scans/port_scanner_report_" + baselineFormattedBeganAt + "_diff_" + formattedBeganAt + "." + saveCorePlugin.getExtension().getDefaultFileExtension());

            saveObject(portScannerDiffFile, _portScannerDiffDetails, Collections.singletonList(Protocol.class));
        }
    }

    private void logDiffDetails(@NonNull final PortScannerDiffDetails _portScannerDiffDetails) {
        final AtomicInteger changeGenerator = new AtomicInteger();

        conditionallyLogCollection(changeGenerator, "New network ids", _portScannerDiffDetails.getNewNetworkIds());
        conditionallyLogCollection(changeGenerator, "Removed network ids", _portScannerDiffDetails.getRemovedNetworkIds());
        conditionallyLogCollection(changeGenerator, "Network ids with changes", _portScannerDiffDetails.getChangedNetworkIds());

        conditionallyLogOpenHostsMap(changeGenerator, "New hosts with open ports discovered", _portScannerDiffDetails.getNewOpenHostsMap());
        conditionallyLogOpenHostsMap(changeGenerator, "Hosts no longer with open ports", _portScannerDiffDetails.getRemovedOpenHostsMap());
        conditionallyLogOpenHostsMap(changeGenerator, "Hosts with open port changes", _portScannerDiffDetails.getChangedOpenHostsMap());

        conditionallyLogOpenPortsTree(changeGenerator, "New open ports discovered", _portScannerDiffDetails.getNewOpenPortsTree());
        conditionallyLogOpenPortsTree(changeGenerator, "Ports no longer open", _portScannerDiffDetails.getRemovedOpenPortsTree());
    }

    private static void conditionallyLogCollection(@NonNull final AtomicInteger _generator,
                                                   @NonNull final String _label,
                                                   @NonNull final Collection _collection) {
        if (!_collection.isEmpty()) {
            final String changePrefix = generateNextChangePrefix(_generator, _label);

            log.warn(changePrefix.concat(": {}"), _collection.toString());
        }
    }

    private static void conditionallyLogOpenHostsMap(@NonNull final AtomicInteger _generator,
                                                     @NonNull final String _label,
                                                     @NonNull final Map<String, List<String>> _map) {
        if (!_map.isEmpty()) {
            for(final Map.Entry<String, List<String>> entry : _map.entrySet()) {
                final String changePrefix = generateNextChangePrefix(_generator, _label);

                log.warn(changePrefix.concat(": networkId={}, hosts={}"), entry.getKey(), entry.getValue().toString());
            }
        }
    }

    private void conditionallyLogOpenPortsTree(@NonNull final AtomicInteger _generator,
                                                      @NonNull final String _label,
                                                      @NonNull final Map<String, Map<String, Map<Protocol, List<Integer>>>> _tree) {
        if (!_tree.isEmpty()) {
            for(Map.Entry<String, Map<String, Map<Protocol, List<Integer>>>> networkSet : _tree.entrySet()) {
                for(final Map.Entry<String, Map<Protocol, List<Integer>>> openHostSet : networkSet.getValue().entrySet()) {
                    for(final Map.Entry<Protocol, List<Integer>> openPortSet : openHostSet.getValue().entrySet()) {
                        final String changePrefix = generateNextChangePrefix(_generator, _label);
                        final List<String> portNumbersWithServices = decoratePortNumbersWithServiceNames(openPortSet.getKey(), openPortSet.getValue());

                        log.warn(changePrefix.concat(": networkId={}, host={}, protocol={}, ports={}"), networkSet.getKey(), openHostSet.getKey(), openPortSet.getKey(), portNumbersWithServices);
                    }
                }
            }
        }
    }

    private List<String> decoratePortNumbersWithServiceNames(final Protocol _protocol, final List<Integer> _portNumbers) {
        final List<String> result = new ArrayList<>();

        for(Integer portNumber : _portNumbers) {
            final Optional<List<IanaServiceEntry>> ianaServiceEntries = knownPortsLookupService.getServiceByName(Port.from(_protocol, portNumber));

            if (ianaServiceEntries.isPresent()) {
                final String serviceNames = ianaServiceEntries.get().stream()
                        .map(s -> s.getServiceName())
                        .collect(Collectors.joining(", "));

                result.add(String.valueOf(portNumber) + " (" + serviceNames + ")");
            } else {
                result.add(String.valueOf(portNumber));
            }
        }

        return result;
    }

    private static String generateNextChangePrefix(@NonNull final AtomicInteger _generator,
                                                   @NonNull final String _label) {
        return "Change #" + _generator.incrementAndGet() + " -> " + _label;
    }

    private void saveObject(@NonNull final File _portScannerResultFile,
                            @NonNull final Object _objectToPersist,
                            @NonNull final List<Class<?>> _asStringClasses) {
        try {
            FileUtils.forceMkdirParent(_portScannerResultFile);
            final boolean success = _portScannerResultFile.createNewFile();

            if (!success) {
                log.debug("Output file already exists: {}", _portScannerResultFile.getPath());
            }

            @Cleanup final FileOutputStream fos = new FileOutputStream(_portScannerResultFile);

            log.info("Saving file: " + _portScannerResultFile.getPath());

            saveCorePlugin.getExtension().save(fos, _objectToPersist, _asStringClasses);
        } catch (IOException _e) {
            log.error("Failed to save port scan result", _e);
        }
    }
}
