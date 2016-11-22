package it.flipb.theapp.domain.service.scanning.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.flipb.theapp.domain.model.scanning.*;
import it.flipb.theapp.domain.service.scanning.PortScannerService;
import it.flipb.theapp.infrastructure.service.dispatcher.CommandDispatcher;
import org.nmap4j.data.NMapRun;
import org.nmap4j.data.host.Address;
import org.nmap4j.data.nmaprun.Host;
import org.nmap4j.parser.OnePassParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
class NmapPortScannerServiceImpl implements PortScannerService {
    private static final String NMAP_XML_RESULT_FILENAME = "/tmp/nmap_scan_result.xml";

    private static final Logger logger = LoggerFactory.getLogger(NmapPortScannerServiceImpl.class);

    private final CommandDispatcher commandDispatcher;

    @Autowired
    NmapPortScannerServiceImpl(@Qualifier("configuredCommandDispatcher") final CommandDispatcher _commandDispatcher) {
        commandDispatcher = _commandDispatcher;
    }

    @Override
    @NotNull
    public PortScannerResult scan(final PortScannerJob _request) {
        final String[] commandWithArguments = buildNmapCommandWithArguments(_request);

        logger.info("Performing nmap scan on: " + _request.getDescription());

        final byte result[] = commandDispatcher.dispatch("nmap", commandWithArguments, NMAP_XML_RESULT_FILENAME);

        return mapXmlToPortScannerResult(_request.getDescription(), result);
    }

    @NotNull
    private String[] buildNmapCommandWithArguments(@NotNull final PortScannerJob _request) {
        final String nmapPortRanges = _request.getPortRanges()
                .stream()
                .map(PortRange::toSingleOrIntervalString)
                .collect(Collectors.joining(","));

        final List<String> targetHosts = _request.getAddresses()
                .stream()
                .map(InetAddress::getHostAddress)
                .collect(Collectors.toList());

        final List<String> argumentsList = Lists.newArrayList("-oX", NMAP_XML_RESULT_FILENAME, "-p", nmapPortRanges);
        argumentsList.addAll(targetHosts);

        return argumentsList.toArray(new String[argumentsList.size()]);
    }

    @NotNull
    public PortScannerResult mapXmlToPortScannerResult(@NotNull final String _description,
                                                       @Null final byte[] _result) {
        final Map<InetAddress, List<Port>> openPortsMap = Maps.newHashMap();

        if (_result != null) {
            final OnePassParser opp = new OnePassParser();
            final NMapRun nmapRun = opp.parse(new String(_result), OnePassParser.STRING_INPUT);

            for(Host host : nmapRun.getHosts()) {
                final List<Port> openPorts = host.getPorts().getPorts().stream()
                        .filter(port -> port.getState().getState().equals("open"))
                        .map(port -> Port.from(mapProtocol(port.getProtocol()), (int) port.getPortId()))
                        .collect(Collectors.toList());

                if (openPorts.isEmpty()) {
                    // nothing to see here, carry on, please... :)
                    continue;
                }

                for(Address address : host.getAddresses()) {
                    try {
                        final InetAddress inetAddress = InetAddress.getByName(address.getAddr());
                        openPortsMap.put(inetAddress, openPorts);
                    } catch (UnknownHostException _e) {
                        // ignore
                    }
                }
            }
        }

        return new PortScannerResult(_description, openPortsMap);
    }

    private Protocol mapProtocol(final String _protocol) {
        switch(_protocol) {
            case "tcp":
                return Protocol.TCP;
            case "udp":
                return Protocol.UDP;
            case "stcp":
                return Protocol.STCP;
            case "ip":
                return Protocol.IP;
            default:
                throw new IllegalArgumentException("Unknown protocol: " + _protocol);
        }
    }
}
