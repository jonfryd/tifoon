package com.elixlogic.tifoon.domain.service.scanner.impl;

import com.elixlogic.tifoon.domain.model.network.IanaServiceEntry;
import com.elixlogic.tifoon.domain.model.scanner.Port;
import com.elixlogic.tifoon.domain.model.scanner.Protocol;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerLoggingService;
import com.elixlogic.tifoon.domain.service.scanner.WellKnownPortsLookupService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PortScannerLoggingServiceImpl implements PortScannerLoggingService {
    private final WellKnownPortsLookupService wellKnownPortsLookupService;

    @Autowired
    public PortScannerLoggingServiceImpl(final WellKnownPortsLookupService _wellKnownPortsLookupService) {
        wellKnownPortsLookupService = _wellKnownPortsLookupService;
    }

    @Override
    public void logDiffDetails(final PortScannerDiffDetails _portScannerDiffDetails) {
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
            final Optional<List<IanaServiceEntry>> ianaServiceEntries = wellKnownPortsLookupService.getServiceByName(Port.from(_protocol, portNumber));

            if (ianaServiceEntries.isPresent()) {
                final String serviceNames = ianaServiceEntries.get().stream()
                        .map(IanaServiceEntry::getServiceName)
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
}
