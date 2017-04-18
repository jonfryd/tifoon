package com.elixlogic.tifoon.domain.mapper.scanner;

import com.elixlogic.tifoon.domain.model.network.Target;
import com.elixlogic.tifoon.domain.model.scanner.Host;
import com.elixlogic.tifoon.domain.model.scanner.Protocol;
import com.elixlogic.tifoon.domain.model.scanner.PortRange;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import lombok.NonNull;
import org.modelmapper.AbstractConverter;
import org.modelmapper.internal.util.Assert;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TargetPortScannerJobMapper extends AbstractConverter<Target, PortScannerJob> {
    @Override
    protected PortScannerJob convert(@NonNull final Target _target) {
        final List<PortRange> portRanges = _target.getPorts()
                .stream()
                .map(this::convertStringToPortRanges)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        final List<Host> hostnames = _target.getAddresses()
                .stream()
                .map(adr -> new Host(adr.getHostName(), adr.getHostAddress()))
                .collect(Collectors.toList());

        return new PortScannerJob(_target.getNetworkId(), hostnames, portRanges);
    }

    private List<PortRange> convertStringToPortRanges(@NonNull final String _stringRange) {
        final String[] protocolPrefix = _stringRange.split(":");
        Assert.isTrue(protocolPrefix.length >= 1 && protocolPrefix.length <= 2, "no more than one protocol prefix allowed");

        final String[] tokens = protocolPrefix[protocolPrefix.length - 1].split("-");
        Assert.isTrue(tokens.length >= 1 && tokens.length <= 2, "one or two port tokens required");

        final Set<Protocol> protocols = new HashSet<>();

        if (protocolPrefix.length == 2) {
            switch (protocolPrefix[0]) {
                case "T":
                case "TCP":
                    protocols.add(Protocol.TCP);
                    break;
                case "U":
                case "UDP":
                    protocols.add(Protocol.UDP);
                    break;
                case "S":
                case "SCPT":
                    protocols.add(Protocol.SCTP);
                    break;
                case "A":
                case "ALL":
                    protocols.add(Protocol.TCP);
                    protocols.add(Protocol.UDP);
                    protocols.add(Protocol.SCTP);
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown protocol prefix: %s", protocolPrefix[0]));
            }
        } else {
            protocols.add(Protocol.TCP);
        }

        if (tokens.length == 1) {
            return protocols
                    .stream()
                    .map(p -> PortRange.singular(p, Integer.parseInt(tokens[0])))
                    .collect(Collectors.toList());
        }

        return protocols
                .stream()
                .map(p -> PortRange.from(p, Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])))
                .collect(Collectors.toList());
    }
}
