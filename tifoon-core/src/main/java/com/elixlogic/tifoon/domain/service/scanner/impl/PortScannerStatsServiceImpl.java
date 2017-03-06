package com.elixlogic.tifoon.domain.service.scanner.impl;

import com.elixlogic.tifoon.domain.model.scanner.NetworkResult;
import com.elixlogic.tifoon.domain.model.scanner.OpenHost;
import com.elixlogic.tifoon.domain.model.scanner.Port;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.model.scanner.diff.*;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerStatsService;
import com.elixlogic.tifoon.domain.util.TimeHelper;
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

        final Set<String> newOpenHostKeys = mapOpenHostsToKeys(newOpenHostPropertyChanges, _newPortScannerResult);
        final Set<String> removedOpenHostKeys = mapOpenHostsToKeys(removedOpenHostPropertyChanges, _newPortScannerResult);

        // find open ports added / removed
        final List<PropertyChange> newOpenPortPropertyChanges = _portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults/\\d+/openHosts/.*", "openPorts", null, Type.MAP, Operation.ADDITION);
        final List<PropertyChange> removedOpenPortPropertyChanges = _portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults/\\d+/openHosts/.*", "openPorts", null, Type.MAP, Operation.REMOVAL);

        final Set<String> newOpenPortKeys = mapOpenPortsToKeys(newOpenPortPropertyChanges, _newPortScannerResult);
        final Set<String> removedOpenPortKeys = mapOpenPortsToKeys(removedOpenPortPropertyChanges, _oldPortScannerResult);

        // determine changed open hosts
        final Set<String> allOpenPortKeys = new HashSet<>(newOpenPortKeys);
        allOpenPortKeys.addAll(removedOpenPortKeys);

        final Set<String> changedOpenHostKeys = new TreeSet<>();

        for(final String key : allOpenPortKeys) {
            final StringJoiner stringJoiner = new StringJoiner("/");
            final String[] keySplits = key.split("/");

            changedOpenHostKeys.add(stringJoiner.add(keySplits[0]).add(keySplits[1]).toString());
        }

        // determine changed networks
        final Set<String> allOpenHostKeys = new HashSet<>(newOpenHostKeys);
        allOpenHostKeys.addAll(removedOpenHostKeys);
        allOpenHostKeys.addAll(changedOpenHostKeys);

        final Set<String> changedNetworkIds = new TreeSet<>();

        for(final String key : allOpenHostKeys) {
            final String[] keySplits = key.split("/");

            changedNetworkIds.add(keySplits[0]);
        }

        return PortScannerDiffDetails.builder()
                .oldPortScannerResultId(_oldPortScannerResult.getId())
                .oldPortScanBeganAtTimestamp(TimeHelper.formatTimestamp(_oldPortScannerResult.getBeganAt()))
                .newPortScannerResultId(_newPortScannerResult.getId())
                .newPortScanBeganAtTimestamp(TimeHelper.formatTimestamp(_newPortScannerResult.getBeganAt()))
                .newNetworkIds(newNetworkIds.stream().collect(Collectors.toList()))
                .removedNetworkIds(removedNetworkIds.stream().collect(Collectors.toList()))
                .changedNetworkIds(changedNetworkIds.stream().collect(Collectors.toList()))
                .newOpenHostKeys(newOpenHostKeys.stream().collect(Collectors.toList()))
                .removedOpenHostKeys(removedOpenHostKeys.stream().collect(Collectors.toList()))
                .changedOpenHostKeys(changedOpenHostKeys.stream().collect(Collectors.toList()))
                .newOpenPortKeys(newOpenPortKeys.stream().collect(Collectors.toList()))
                .removedOpenPortKeys(removedOpenPortKeys.stream().collect(Collectors.toList()))
                .build();
    }

    private Set<String> mapNetworkResultsToIds(@NonNull final List<PropertyChange> _propertyChanges,
                                               @NonNull final PortScannerResult _portScannerResult) {
        return new TreeSet<>(_propertyChanges.stream()
                .map(p -> getNetworkId(p, _portScannerResult))
                .collect(Collectors.toList()));
    }

    private Set<String> mapOpenPortsToKeys(@NonNull final List<PropertyChange> _propertyChanges,
                                            @NonNull final PortScannerResult _portScannerResult) {
        final List<String> result = new ArrayList<>();

        for(final PropertyChange propertyChange : _propertyChanges) {
            final Pair<String, OpenHost> openHost = getOpenHost(propertyChange, _portScannerResult);
            final int portKey = Integer.parseInt(propertyChange.getKey());

            result.add(createOpenPortKey(openHost.getKey(), openHost.getValue().getOpenPorts().get(portKey), propertyChange, _portScannerResult));
        }

        return new TreeSet<>(result);
    }

    private String createOpenPortKey(@NonNull final String _openHost,
                                     @NonNull final Port _port,
                                     @NonNull final PropertyChange _propertyChange,
                                     @NonNull final PortScannerResult _portScannerResult) {
        final StringJoiner stringJoiner = new StringJoiner("/");

        return stringJoiner
                .add(getNetworkId(_propertyChange, _portScannerResult))
                .add(_openHost)
                .add(_port.getProtocol().toString())
                .add(String.valueOf(_port.getPortNumber()))
                .toString();
    }

    private Set<String> mapOpenHostsToKeys(@NonNull final List<PropertyChange> _propertyChanges,
                                           @NonNull final PortScannerResult _portScannerResult) {
        return new TreeSet<>(_propertyChanges.stream()
                .map(p -> createOpenHostKey(p.getKey(), p, _portScannerResult))
                .collect(Collectors.toList()));
    }

    private String createOpenHostKey(@NonNull final String _openHost,
                                     @NonNull final PropertyChange _propertyChange,
                                     @NonNull final PortScannerResult _portScannerResult) {
        final StringJoiner stringJoiner = new StringJoiner("/");

        return stringJoiner
                .add(getNetworkId(_propertyChange, _portScannerResult))
                .add(_openHost)
                .toString();
    }

    private String getNetworkId(@NonNull final PropertyChange _propertyChange,
                                @NonNull final PortScannerResult _portScannerResult) {
        final NetworkResult networkResult = getNetworkResult(_propertyChange, _portScannerResult);

        return networkResult.getNetworkId();
    }

    private NetworkResult getNetworkResult(@NonNull final PropertyChange _propertyChange,
                                           @NonNull final PortScannerResult _portScannerResult) {
        final Pattern pattern = Pattern.compile("#networkResults/\\d+");
        final Matcher matcher = pattern.matcher(_propertyChange.getGlobalId().getSelector());
        Assert.isTrue(matcher.find(), "Could not find network result in selector");

        final int resultIndex = Integer.valueOf(matcher.group().split("/")[1]);
        return _portScannerResult.getNetworkResults().get(resultIndex);
    }

    private Pair<String, OpenHost> getOpenHost(@NonNull final PropertyChange _propertyChange,
                                               @NonNull final PortScannerResult _portScannerResult) {
        final Pattern pattern = Pattern.compile("#networkResults/\\d+/openHosts/.*");
        final Matcher matcher = pattern.matcher(_propertyChange.getGlobalId().getSelector());
        Assert.isTrue(matcher.find(), "Could not find open host in selector");

        final String openHost = matcher.group().split("/")[3];

        return Pair.of(openHost, getNetworkResult(_propertyChange, _portScannerResult).getOpenHosts().get(openHost));
    }
}
