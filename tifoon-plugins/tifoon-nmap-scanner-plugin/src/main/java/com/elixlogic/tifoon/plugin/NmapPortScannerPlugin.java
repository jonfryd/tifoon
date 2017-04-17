package com.elixlogic.tifoon.plugin;

import com.elixlogic.tifoon.domain.model.scanner.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.elixlogic.tifoon.plugin.executer.ExecutorPlugin;
import com.elixlogic.tifoon.plugin.scanner.AbstractScannerPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.nmap4j.data.NMapRun;
import org.nmap4j.data.host.Address;
import org.nmap4j.data.nmaprun.Host;
import org.nmap4j.parser.OnePassParser;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class NmapPortScannerPlugin extends AbstractScannerPlugin {
    private static final String PROVIDES = "nmap";

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    public NetworkResult scan(@NonNull final PortScannerJob _request,
                              @NonNull final ExecutorPlugin _executorPlugin,
                              @Nullable final String _additionalParameters) {
        try {
            final String scanResultFilename = String.format("nmap_scan_result_%s.xml", UUID.randomUUID().toString());
            final String[] commandWithArguments = buildNmapCommandWithArguments(_request, scanResultFilename, _additionalParameters);
            final byte result[] = _executorPlugin.dispatch("nmap", commandWithArguments, scanResultFilename);

            return mapXmlToPortScannerResult(_request, result);
        }
        catch (Exception _e) {
            log.error("Error running nmap", _e);

            return new NetworkResult(_request.getNetworkId(), false, Collections.EMPTY_MAP);
        }
    }

    private String[] buildNmapCommandWithArguments(@NonNull final PortScannerJob _request,
                                                   @NonNull final String _scanResultFilename,
                                                   @Nullable final String _additionalParameters) {
        final String nmapPortRanges = _request.getPortRanges()
                .stream()
                .map(PortRange::toSingleOrIntervalString)
                .collect(Collectors.joining(","));

        final List<String> targetHosts = _request.getHosts()
                .stream()
                .map(com.elixlogic.tifoon.domain.model.scanner.Host::getHostAddress)
                .collect(Collectors.toList());

        final List<String> additionalParameters = Stream.of(Optional.ofNullable(_additionalParameters).orElse("")
                .split(" "))
                .collect(Collectors.toList());

        final List<String> argumentsList = Lists.newArrayList("-oX", _scanResultFilename, "-p", nmapPortRanges);
        argumentsList.addAll(targetHosts);
        argumentsList.addAll(0, additionalParameters);

        return argumentsList.toArray(new String[argumentsList.size()]);
    }

    private NetworkResult mapXmlToPortScannerResult(@NonNull final PortScannerJob _request,
                                                    @Nullable final byte[] _result) {
        if (_result == null) {
            return new NetworkResult(_request.getNetworkId(), false, Collections.EMPTY_MAP);
        }

        final Map<InetAddress, List<Port>> openPortsMap = Maps.newHashMap();

        final OnePassParser opp = new OnePassParser();
        final NMapRun nmapRun = opp.parse(new String(_result, StandardCharsets.UTF_8), OnePassParser.STRING_INPUT);

        if (nmapRun != null) {
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

        return new NetworkResult(_request.getNetworkId(), true, openPortsMap);
    }

    private Protocol mapProtocol(@NonNull final String _protocol) {
        switch(_protocol) {
            case "tcp":
                return Protocol.TCP;
            case "udp":
                return Protocol.UDP;
            case "stcp":
                return Protocol.SCTP;
            case "ip":
                return Protocol.IP;
            default:
                throw new IllegalArgumentException("Unknown protocol: " + _protocol);
        }
    }
}
