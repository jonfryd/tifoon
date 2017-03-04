package com.elixlogic.tifoon;

import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerResultDiffService;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import com.elixlogic.tifoon.application.config.RootConfiguration;
import com.elixlogic.tifoon.domain.model.plugin.CorePlugin;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.mapper.DtoMapper;
import com.elixlogic.tifoon.infrastructure.config.PluginConfiguration;
import com.elixlogic.tifoon.plugin.io.IoPlugin;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@SuppressFBWarnings("OBL_UNSATISFIED_OBLIGATION") // https://github.com/findbugsproject/findbugs/issues/98
@Slf4j
public class Main {
    private final RootConfiguration configuration;
    private final DtoMapper dtoMapper;
    private final PortScannerService portScannerService;
    private final PortScannerResultDiffService portScannerResultDiffService;
    private final PluginConfiguration pluginConfiguration;
    private final CorePlugin<IoPlugin> ioCorePlugin;

    @Autowired
    public Main(final RootConfiguration _configuration,
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

    @Bean
    public CommandLineRunner scan() {
        return (args) -> {
            if (configuration.getMasterPlan().getScanner().isActive()) {
                if (!pluginConfiguration.verify()) {
                    return;
                }

                final PortScannerResult portScannerResult1 = doScanAndSaveAndReload("1");
                final PortScannerResult portScannerResult2 = doScanAndSaveAndReload("2");
                final PortScannerResult portScannerResult3 = load("3");

                final PortScannerDiff portScannerDiff12 = portScannerResultDiffService.diff(portScannerResult1, portScannerResult2);

                log.info("Diff run 1 and 2: {}", portScannerDiff12);

                final PortScannerDiff portScannerDiff23 = portScannerResultDiffService.diff(portScannerResult2, portScannerResult3);

                log.info("Diff run 2 and 3 (from disk): {}", portScannerDiff23);

                final File diffFile = new File("diff_report." + ioCorePlugin.getSupports());
                diffFile.createNewFile();

                @Cleanup final FileOutputStream fos2 = new FileOutputStream(diffFile);
                ioCorePlugin.getExtension().save(fos2, portScannerDiff23);

                //System.out.println(portScannerDiff23.findPropertyChanges(PortScannerResult.class, ".*/port", null, null, null, null));

                @Cleanup final FileInputStream fis2 = new FileInputStream(diffFile);
                final PortScannerDiff resultsFromDiffFile = ioCorePlugin.getExtension().load(fis2, PortScannerDiff.class);

                //System.out.println(resultsFromDiffFile);

                log.info("Persisted diff equals original diff: " + resultsFromDiffFile.equals(portScannerDiff23));

                // TODO: do something useful with the results - e.g. test for exploits
            } else {
                log.info("Scanning not enabled.");
            }
        };
    }

    private PortScannerResult doScanAndSaveAndReload(final String _suffix) throws IOException {
        log.info("Performing parallel port scan.");

        final List<PortScannerJob> portScannerJobs = configuration.getNetwork().getTargets()
                .stream()
                .map(target -> dtoMapper.map(target, PortScannerJob.class))
                .collect(Collectors.toList());
        final PortScannerResult portScannerResult = portScannerService.scanAndPersist(portScannerJobs);

        //_portScannerResultRepository.save(portScannerResult);
        //System.out.println(portScannerResult);
        //final PortScannerResult portScannerResult = _portScannerResultRepository.findOne(portScannerResult.getPortScannerResultId());

        final File file = new File("port_scanner_report_" + _suffix + "." + ioCorePlugin.getSupports());
        final boolean success = file.createNewFile();

        if (!success) {
            log.debug("output file already exists: {}", file.getPath());
        }

        @Cleanup FileOutputStream fos = new FileOutputStream(file);
        ioCorePlugin.getExtension().save(fos, portScannerResult);

        @Cleanup final FileInputStream fis = new FileInputStream(file);
        final PortScannerResult resultsFromFile = ioCorePlugin.getExtension().load(fis, PortScannerResult.class);

        //System.out.println(resultsFromFile);

        log.info("Persisted results equals original results: " + resultsFromFile.equals(portScannerResult));

        return resultsFromFile;
    }

    private PortScannerResult load(final String _suffix) throws IOException {
        final File file = new File("port_scanner_report_" + _suffix + "." + ioCorePlugin.getSupports());

        @Cleanup final FileInputStream fis = new FileInputStream(file);

        return ioCorePlugin.getExtension().load(fis, PortScannerResult.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
