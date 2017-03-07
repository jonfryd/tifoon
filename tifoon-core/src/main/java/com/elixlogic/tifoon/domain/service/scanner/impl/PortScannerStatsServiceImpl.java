package com.elixlogic.tifoon.domain.service.scanner.impl;

import com.elixlogic.tifoon.domain.model.scanner.*;
import com.elixlogic.tifoon.domain.model.scanner.diff.*;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerStatsService;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.modelmapper.internal.util.Assert;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PortScannerStatsServiceImpl implements PortScannerStatsService {
    @Override
    public PortScannerDiffDetails createDetails(@NonNull final PortScannerResult _oldPortScannerResult,
                                                @NonNull final PortScannerResult _newPortScannerResult,
                                                @NonNull final PortScannerDiff _portScannerDiff) {
        // find networks added / removed
        final List<PropertyChange> newNetworkResultPropertyChanges = _portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults/\\d+", null, null, Type.OBJECT, Operation.ADDITION);
        final List<PropertyChange> removedNetworkResultPropertyChanges = _portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults/\\d+", null, null, Type.OBJECT, Operation.REMOVAL);

        final Set<String> newNetworkIds = mapNetworkResultsToIds(newNetworkResultPropertyChanges, _newPortScannerResult);
        final Set<String> removedNetworkIds = mapNetworkResultsToIds(removedNetworkResultPropertyChanges, _oldPortScannerResult);

        // find open hosts added / removed
        final List<PropertyChange> newOpenHostPropertyChanges = _portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults/\\d+", "openHosts", null, Type.MAP, Operation.ADDITION);
        final List<PropertyChange> removedOpenHostPropertyChanges = _portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults/\\d+", "openHosts", null, Type.MAP, Operation.REMOVAL);

        final Multimap<String, String> newOpenHostsMultimap = mapOpenHosts(newOpenHostPropertyChanges, _newPortScannerResult);
        final Multimap<String, String> removedOpenHostsMultimap = mapOpenHosts(removedOpenHostPropertyChanges, _newPortScannerResult);

        // find open ports added / removed
        final List<PropertyChange> newOpenPortPropertyChanges = _portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults/\\d+/openHosts/.*", "openPorts", null, Type.MAP, Operation.ADDITION);
        final List<PropertyChange> removedOpenPortPropertyChanges = _portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults/\\d+/openHosts/.*", "openPorts", null, Type.MAP, Operation.REMOVAL);

        final Map<String, Map<String, Multimap<Protocol, Integer>>> newOpenPortsTree = mapOpenPorts(newOpenPortPropertyChanges, _newPortScannerResult);
        final Map<String, Map<String, Multimap<Protocol, Integer>>> removedOpenPortsTree = mapOpenPorts(removedOpenPortPropertyChanges, _oldPortScannerResult);

        // determine changed open hosts
        final Multimap<String, String> changedOpenHostsMultimap = TreeMultimap.create();

        for(final Map.Entry<String, Map<String, Multimap<Protocol, Integer>>> entry : newOpenPortsTree.entrySet()) {
            changedOpenHostsMultimap.putAll(entry.getKey(), entry.getValue().keySet());
        }
        for(final Map.Entry<String, Map<String, Multimap<Protocol, Integer>>> entry : removedOpenPortsTree.entrySet()) {
            changedOpenHostsMultimap.putAll(entry.getKey(), entry.getValue().keySet());
        }

        // determine changed networks
        final Set<String> allOpenHostKeys = new HashSet<>(newOpenHostsMultimap.keySet());
        allOpenHostKeys.addAll(removedOpenHostsMultimap.keySet());
        allOpenHostKeys.addAll(changedOpenHostsMultimap.keySet());

        final Set<String> changedNetworkIds = new TreeSet<>();

        for(final String key : allOpenHostKeys) {
            changedNetworkIds.add(key);
        }

        return PortScannerDiffDetails.builder()
                .oldPortScannerResultId(_oldPortScannerResult.getId())
                .oldPortScanBeganAt(_oldPortScannerResult.getBeganAt())
                .newPortScannerResultId(_newPortScannerResult.getId())
                .newPortScanBeganAt(_newPortScannerResult.getBeganAt())
                .newNetworkIds(newNetworkIds.stream().collect(Collectors.toList()))
                .removedNetworkIds(removedNetworkIds.stream().collect(Collectors.toList()))
                .changedNetworkIds(changedNetworkIds.stream().collect(Collectors.toList()))
                .newOpenHostsMap(toMapOfLists(newOpenHostsMultimap))
                .removedOpenHostsMap(toMapOfLists(removedOpenHostsMultimap))
                .changedOpenHostsMap(toMapOfLists(changedOpenHostsMultimap))
                .newOpenPortsTree(toStringListTree(newOpenPortsTree))
                .removedOpenPortsTree(toStringListTree(removedOpenPortsTree))
                .build();
    }

    private static Map<String, Map<String, Map<Protocol, List<Integer>>>> toStringListTree(@NonNull final Map<String, Map<String, Multimap<Protocol, Integer>>> _map) {
        // yeah... it sure ain't super pretty, but gets the job done!
        final Map<String, Map<String, Map<Protocol, List<Integer>>>> result = new TreeMap<>();

        for(final Map.Entry<String, Map<String, Multimap<Protocol, Integer>>> networkSet : _map.entrySet()) {
            result.put(networkSet.getKey(), new TreeMap<>());
            final Map<String, Map<Protocol, List<Integer>>> openHostToOpenPorts = result.get(networkSet.getKey());

            for(final Map.Entry<String, Multimap<Protocol, Integer>> openHostSet : networkSet.getValue().entrySet()) {
                openHostToOpenPorts.put(openHostSet.getKey(), toMapOfLists(openHostSet.getValue()));
            }
        }

        return result;
    }

    private static <K, V> Map<K, List<V>> toMapOfLists(@NonNull final Multimap<K, V> _multimap) {
        final Map<K, List<V>> mapOfLists = new TreeMap<>();
        _multimap.asMap().forEach((k, v) -> mapOfLists.put(k, new ArrayList<>(v)));

        return mapOfLists;
    }

    private static Set<String> mapNetworkResultsToIds(@NonNull final List<PropertyChange> _propertyChanges,
                                                      @NonNull final PortScannerResult _portScannerResult) {
        return new TreeSet<>(_propertyChanges.stream()
                .map(p -> getNetworkId(p, _portScannerResult))
                .collect(Collectors.toList()));
    }

    private static Map<String, Map<String, Multimap<Protocol, Integer>>> mapOpenPorts(@NonNull final List<PropertyChange> _propertyChanges,
                                                                                      @NonNull final PortScannerResult _portScannerResult) {
        final Map<String, Map<String, Multimap<Protocol, Integer>>> result = new TreeMap<>();

        for(final PropertyChange propertyChange : _propertyChanges) {
            final String networkId = getNetworkId(propertyChange, _portScannerResult);
            final Pair<String, OpenHost> openHost = getOpenHost(propertyChange, _portScannerResult);
            final Port port = openHost.getValue().getOpenPorts().get(Integer.parseInt(propertyChange.getKey()));

            result.putIfAbsent(networkId, new TreeMap<>());
            final Map<String, Multimap<Protocol, Integer>> openHostToOpenPortsMultimap = result.get(networkId);

            openHostToOpenPortsMultimap.putIfAbsent(openHost.getKey(), TreeMultimap.create());
            final Multimap<Protocol, Integer> openPortsMultimap = openHostToOpenPortsMultimap.get(openHost.getKey());

            openPortsMultimap.put(port.getProtocol(), port.getPortNumber());
        }

        return result;
    }

    private static Multimap<String, String> mapOpenHosts(@NonNull final List<PropertyChange> _propertyChanges,
                                                         @NonNull final PortScannerResult _portScannerResult) {
        final TreeMultimap<String, String> result = TreeMultimap.create();
        _propertyChanges.forEach(p -> result.put(getNetworkId(p, _portScannerResult), p.getKey()));

        return result;
    }

    private static String getNetworkId(@NonNull final PropertyChange _propertyChange,
                                       @NonNull final PortScannerResult _portScannerResult) {
        return getNetworkResult(_propertyChange, _portScannerResult).getNetworkId();
    }

    private static NetworkResult getNetworkResult(@NonNull final PropertyChange _propertyChange,
                                                  @NonNull final PortScannerResult _portScannerResult) {
        final Pattern pattern = Pattern.compile("#networkResults/\\d+");
        final Matcher matcher = pattern.matcher(_propertyChange.getGlobalId().getSelector());
        Assert.isTrue(matcher.find(), "Could not find network result in selector");

        final Integer resultIndex = Integer.valueOf(matcher.group().split("/")[1]);
        return _portScannerResult.getNetworkResults().get(resultIndex);
    }

    private static Pair<String, OpenHost> getOpenHost(@NonNull final PropertyChange _propertyChange,
                                                      @NonNull final PortScannerResult _portScannerResult) {
        final Pattern pattern = Pattern.compile("#networkResults/\\d+/openHosts/.*");
        final Matcher matcher = pattern.matcher(_propertyChange.getGlobalId().getSelector());
        Assert.isTrue(matcher.find(), "Could not find open host in selector");

        final String openHost = matcher.group().split("/")[3];

        return Pair.of(openHost, getNetworkResult(_propertyChange, _portScannerResult).getOpenHosts().get(openHost));
    }
}
