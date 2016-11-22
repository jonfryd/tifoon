package it.flipb.theapp;

import it.flipb.theapp.application.config.RootConfiguration;
import it.flipb.theapp.domain.model.scanning.PortScannerJob;
import it.flipb.theapp.domain.model.scanning.PortScannerResult;
import it.flipb.theapp.domain.service.scanning.PortScannerService;
import it.flipb.theapp.application.mapper.DtoMapper;
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

    @Autowired
    public Main(@NotNull final RootConfiguration _configuration,
                @NotNull final DtoMapper _dtoMapper,
                @NotNull final PortScannerService _portScannerService) {
        logger.debug(_configuration.getApplication().toString());
        logger.debug(_configuration.getNetwork().toString());

        if (_configuration.getApplication().getScanner().isActive()) {
            logger.info("Performing parallel port scan.");

            List<PortScannerResult> portScannerResults = _configuration.getNetwork().getTargets()
                    .parallelStream()
                    .map(target -> _dtoMapper.map(target, PortScannerJob.class))
                    .map(_portScannerService::scan)
                    .collect(Collectors.toList());

            logger.info(portScannerResults.toString());

            // TODO: do something useful with the results - e.g. test for exploits
        }
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
