package it.flipb.theapp;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.flipb.theapp.application.config.RootConfiguration;
import it.flipb.theapp.domain.model.plugin.CorePlugin;
import it.flipb.theapp.domain.model.scanner.*;
import it.flipb.theapp.domain.model.scanner.diff.PortScannerDiff;
import it.flipb.theapp.domain.service.scanner.PortScannerResultDiffService;
import it.flipb.theapp.domain.service.scanner.PortScannerService;
import it.flipb.theapp.domain.mapper.DtoMapper;
import it.flipb.theapp.domain.util.TimeHelper;
import it.flipb.theapp.infrastructure.config.PluginConfiguration;
import it.flipb.theapp.plugin.io.IoPlugin;
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

                log.info("Performing parallel port scan.");

                final List<PortScannerJob> portScannerJobs = configuration.getNetwork().getTargets()
                        .stream()
                        .map(target -> dtoMapper.map(target, PortScannerJob.class))
                        .collect(Collectors.toList());
                final PortScannerResult portScannerResult = portScannerService.scanAndPersist(portScannerJobs);

                //_portScannerResultRepository.save(portScannerResult);
                //System.out.println(portScannerResult);
                //final PortScannerResult portScannerResult = _portScannerResultRepository.findOne(portScannerResult.getPortScannerResultId());
                System.out.println(portScannerResult);

                final File file = new File("port_scanner_report." + ioCorePlugin.getSupports());
                file.createNewFile();

                @Cleanup FileOutputStream fos = new FileOutputStream(file);
                ioCorePlugin.get().save(fos, portScannerResult);

                @Cleanup final FileInputStream fis = new FileInputStream(file);
                final PortScannerResult resultsFromFile = ioCorePlugin.get().load(fis, PortScannerResult.class);

                log.info("Persisted results equals original results: " + resultsFromFile.equals(portScannerResult));

                // TODO: remove
                //resultsFromFile.getPortScannerResults().get(1).getOpenPortsMap().put("hest", new Ports(Lists.newArrayList(Port.from(Protocol.UDP, 53))));
                final OpenHost openHost = resultsFromFile.getNetworkResults().get(1).getOpenHosts().get(0);
                openHost.setHost("sssdsdsd");
                openHost.getOpenPorts().get(0).getPort().setProtocol(Protocol.STCP);
                openHost.getOpenPorts().get(1).getPort().setPortNumber(123);
                openHost.getOpenPorts().get(0).getPort().getJonTester().getSjover()[1] = 4565;
                openHost.getOpenPorts().get(1).getPort().getJonTester().setSjover(new int[]{1,2,3,-4});
                openHost.getOpenPorts().get(1).getPort().getJonTester().getFisk().put("sdfsa", "sdf");
                openHost.getOpenPorts().get(1).getPort().getJonTester().getFisk().remove("g");
                openHost.getOpenPorts().get(1).getPort().getJonTester().getFisk().put("g5", "zxc");
                System.out.println(openHost);
                final OpenPort openPort = new OpenPort(Port.from(Protocol.UDP, 53));
                System.out.println(openPort);
                openPort.setId(34L);
                System.out.println(openPort);
                openHost.getOpenPorts().add(openPort);

                resultsFromFile.getNetworkResults().get(1).setDescription("blah");
                resultsFromFile.getNetworkResults().remove(0);

                resultsFromFile.setSuccess(false);

                System.out.println(openPort);

                final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult, resultsFromFile);

                System.out.println(openPort);
                System.out.println(TimeHelper.toLocalDateTime(portScannerResult.getBeganAt()));
                System.out.println(TimeHelper.toLocalDateTime(portScannerResult.getEndedAt()));
                System.out.println(portScannerResult.calcExecutionTimeMillis());

                final File diffFile = new File("diff_report." + ioCorePlugin.getSupports());
                diffFile.createNewFile();

                @Cleanup final FileOutputStream fos2 = new FileOutputStream(diffFile);
                ioCorePlugin.get().save(fos2, portScannerDiff);

                @Cleanup final FileInputStream fis2 = new FileInputStream(diffFile);
                final PortScannerDiff resultsFromDiffFile = ioCorePlugin.get().load(fis2, PortScannerDiff.class);

//                    System.out.println(portScannerDiff.getPortsChanged());
//                    System.out.println(resultsFromDiffFile.getPortsChanged());

                log.info("Persisted diff equals original diff: " + resultsFromDiffFile.equals(portScannerDiff));

/*
                if (networkResults.hasResults()) {
                    log.info(networkResults.toString());
                } else {
                    log.warn("No results returned from scanning");
                }
*/
                // TODO: do something useful with the results - e.g. test for exploits
            } else {
                log.info("Scanning not enabled.");
            }
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
