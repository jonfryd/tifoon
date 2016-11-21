package it.flipb.theapp;

import it.flipb.theapp.config.RootConfiguration;
import it.flipb.theapp.domain.model.scanning.PortScannerJob;
import it.flipb.theapp.domain.model.scanning.PortScannerResult;
import it.flipb.theapp.domain.service.scanning.PortScannerService;
import it.flipb.theapp.mapper.DtoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final RootConfiguration configuration;
    private final DtoMapper dtoMapper;
    private final PortScannerService portScannerService;
    
    @Autowired
    public Main(@NotNull final RootConfiguration _configuration,
                @NotNull final DtoMapper _dtoMapper,
                @NotNull final PortScannerService _portScannerService) {
        this.configuration = _configuration;
        dtoMapper = _dtoMapper;
        portScannerService = _portScannerService;

        logger.debug(configuration.getApplication().toString());
        logger.debug(configuration.getNetwork().toString());

        if (configuration.getApplication().isScanning()) {
            logger.info("Performing parallel port scan.");

            List<PortScannerResult> portScannerJobs = configuration.getNetwork().getTargets()
                    .parallelStream()
                    .map(target -> dtoMapper.map(target, PortScannerJob.class))
                    .map(portScannerService::scan)
                    .collect(Collectors.toList());

            // TODO: do something useful with the results - e.g. test for exploits
            logger.debug(portScannerJobs.toString());
        }
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
