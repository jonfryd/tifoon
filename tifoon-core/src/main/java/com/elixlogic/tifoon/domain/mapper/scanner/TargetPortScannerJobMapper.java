package com.elixlogic.tifoon.domain.mapper.scanner;

import com.elixlogic.tifoon.domain.model.network.Target;
import com.elixlogic.tifoon.domain.model.scanner.Host;
import com.elixlogic.tifoon.domain.model.scanner.Protocol;
import com.elixlogic.tifoon.domain.model.scanner.PortRange;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import lombok.NonNull;
import org.modelmapper.AbstractConverter;
import org.modelmapper.internal.util.Assert;

import java.net.InetAddress;
import java.util.List;
import java.util.stream.Collectors;

public class TargetPortScannerJobMapper extends AbstractConverter<Target, PortScannerJob> {
    @Override
    protected PortScannerJob convert(@NonNull final Target _target) {
        final List<PortRange> portRanges = _target.getPorts()
                .stream()
                .map(this::convertStringToPortRange)
                .collect(Collectors.toList());
        final List<Host> hostnames = _target.getAddresses()
                .stream()
                .map(adr -> new Host(adr.getHostName(), adr.getHostAddress()))
                .collect(Collectors.toList());

        return new PortScannerJob(_target.getNetworkId(), hostnames, portRanges);
    }

    private PortRange convertStringToPortRange(@NonNull final String _stringRange) {
        final String[] tokens = _stringRange.split("-");
        Assert.isTrue(tokens.length >= 1 && tokens.length <= 2, "one or two tokens allowed");
        final Protocol protocol = Protocol.TCP;

        if (tokens.length == 1) {
            return PortRange.singular(protocol, Integer.parseInt(tokens[0]));
        }

        return PortRange.from(protocol, Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
    }
}
