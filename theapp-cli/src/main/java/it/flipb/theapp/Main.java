package it.flipb.theapp;

import it.flipb.theapp.application.config.RootConfiguration;
import it.flipb.theapp.domain.model.scanner.PortScannerJob;
import it.flipb.theapp.domain.model.scanner.PortScannerResult;
import it.flipb.theapp.domain.service.scanner.PortScannerService;
import it.flipb.theapp.domain.mapper.DtoMapper;
import it.flipb.theapp.infrastructure.config.PluginConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@SpringBootApplication
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final RootConfiguration configuration;
    private final DtoMapper dtoMapper;
    private final PortScannerService portScannerService;
    private final PluginConfiguration pluginConfiguration;

    @Autowired
    public Main(final RootConfiguration _configuration,
                final DtoMapper _dtoMapper,
                final PortScannerService _portScannerService,
                final PluginConfiguration _pluginConfiguration) {
        configuration = _configuration;
        dtoMapper = _dtoMapper;
        portScannerService = _portScannerService;
        pluginConfiguration = _pluginConfiguration;

        logger.debug(_configuration.getMasterPlan().toString());
        logger.debug(_configuration.getNetwork().toString());
    }

    @EventListener
    public void onApplicationInitialized(final ContextRefreshedEvent _contextRefreshedEvent) {
        if (configuration.getMasterPlan().getScanner().isActive()) {
            if (!pluginConfiguration.verify()) {
                return;
            }

            logger.info("Performing parallel port scan.");

            List<PortScannerResult> portScannerResults = configuration.getNetwork().getTargets()
                    .parallelStream()
                    .map(target -> dtoMapper.map(target, PortScannerJob.class))
                    .map(portScannerService::scan)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!portScannerResults.isEmpty()) {
                logger.info(portScannerResults.toString());
            } else {
                logger.warn("No results returned from scanning");
            }

            // TODO: do something useful with the results - e.g. test for exploits
        } else {
            logger.info("Scanning not enabled.");
        }
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
