package com.elixlogic.tifoon.application.schedulers;

import com.elixlogic.tifoon.application.config.RootConfiguration;
import com.elixlogic.tifoon.domain.mapper.DtoMapper;
import com.elixlogic.tifoon.domain.model.scanner.*;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;
import com.elixlogic.tifoon.domain.service.reporting.ReportingService;
import com.elixlogic.tifoon.domain.service.scanner.*;
import com.elixlogic.tifoon.infrastructure.jpa.repository.PortScannerResultRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.mail.MessagingException;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class PortScanScheduler {
    private final RootConfiguration rootConfiguration;
    private final DtoMapper dtoMapper;
    private final PortScannerService portScannerService;
    private final PortScannerResultRepository portScannerResultRepository;
    private final PortScannerResultDiffService portScannerResultDiffService;
    private final PortScannerStatsService portScannerStatsService;
    private final PortScannerFileIOService portScannerFileIOService;
    private final ReportingService reportingService;

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
                             final PortScannerFileIOService _portScannerFileIOService,
                             final ReportingService _reportingService) throws MessagingException {
        rootConfiguration = _rootConfiguration;
        dtoMapper = _dtoMapper;
        portScannerService = _portScannerService;
        portScannerResultRepository = _portScannerResultRepository;
        portScannerResultDiffService = _portScannerResultDiffService;
        portScannerStatsService = _portScannerStatsService;
        portScannerFileIOService = _portScannerFileIOService;
        reportingService = _reportingService;

        log.debug(_rootConfiguration.getAppSettings().toString());
        log.debug(_rootConfiguration.getCoreSettings().toString());
        log.debug(_rootConfiguration.getNetwork().toString());
    }

    @Scheduled(fixedRateString = "${tifoon.scanRateSeconds}000")
    @Transactional
    public void performScan() {
        if (rootConfiguration.getCoreSettings().getScanner().isActive()) {
            log.info("Scanning...");

            final List<PortScannerJob> portScannerJobs = rootConfiguration.getNetwork().getTargets()
                    .stream()
                    .map(target -> dtoMapper.map(target, PortScannerJob.class))
                    .collect(Collectors.toList());

            // scan and save to in-memory H2 DB repository (generates primary IDs)...
            // For now it's complete overkill to use Spring Data JPA for the sole purpose of generating an ID, but
            // down the line my gut feeling is that it is worth the effort, since we can easily persist
            // scans to physical DBMS (MySQL, PostgreSQL, etc). JPA and JaVers are buddies, which is important, too.
            final PortScannerResult portScannerResult = portScannerResultRepository.save(portScannerService.scan(portScannerJobs, rootConfiguration.getCoreSettings().getScanner().getAdditionalParameters()));

            if (firstScan) {
                baselinePortScannerResult = rootConfiguration.getAppSettings().isUseInitialScanAsBaseline() ?
                        portScannerResult :
                        portScannerFileIOService.loadPortScannerResult(rootConfiguration.getAppSettings().getBaselineFilename(), portScannerResult);
            }

            final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(baselinePortScannerResult, portScannerResult);
            final PortScannerDiffDetails portScannerDiffDetails = portScannerStatsService.createDetails(baselinePortScannerResult, portScannerResult, portScannerDiff);

            boolean saveReport = false;

            if (!rootConfiguration.getAppSettings().isOnlySaveReportOnChange() ||
                    (firstScan && rootConfiguration.getAppSettings().isUseInitialScanAsBaseline())) {
                saveReport = true;
            } else if (!portScannerDiff.isUnchanged()) {
                log.warn("One or more changes DETECTED!");
                saveReport = true;

                if (rootConfiguration.getAppSettings().isDynamicBaselineMode()) {
                    baselinePortScannerResult = portScannerResult;
                    log.info("Dynamic baseline is enabled; baseline changed.");
                }
            } else {
                log.info("No changes detected.");
            }

            if (saveReport) {
                log.info("Saving report.");
                portScannerFileIOService.savePortScannerResults("scans/port_scanner_report_", baselinePortScannerResult, portScannerResult, portScannerDiff, portScannerDiffDetails);

                // save HTML and/or PDF
                reportingService.report(
                        rootConfiguration.getCoreSettings(),
                        rootConfiguration.getAppSettings(),
                        "scans/port_scanner_report_",
                        baselinePortScannerResult,
                        portScannerResult,
                        portScannerDiff,
                        portScannerDiffDetails);
            }

            // clean-up step - remove scan from in-memory repository
            portScannerResultRepository.delete(portScannerResult);

            firstScan = false;

            log.info("Scanning completed.");
        } else {
            log.info("Scanning not enabled.");
        }
    }
}
