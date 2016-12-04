package it.flipb.theapp.domain.mapper.scanner;

import it.flipb.theapp.domain.model.network.Target;
import it.flipb.theapp.domain.model.scanner.PortRange;
import it.flipb.theapp.domain.model.scanner.PortScannerJob;
import it.flipb.theapp.domain.model.scanner.Protocol;
import org.modelmapper.AbstractConverter;
import org.modelmapper.internal.util.Assert;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class TargetPortScannerJobMapper extends AbstractConverter<Target, PortScannerJob> {
    @Override
    protected PortScannerJob convert(final Target _target) {
        final List<PortRange> portRanges = _target.getPorts()
                .stream()
                .map(this::convertStringToPortRange)
                .collect(Collectors.toList());

        return new PortScannerJob(_target.getDescription(), _target.getAddresses(), portRanges);
    }

    private PortRange convertStringToPortRange(@NotNull final String _stringRange) {
        final String[] tokens = _stringRange.split("-");
        Assert.isTrue(tokens.length >= 1 && tokens.length <= 2, "one or two tokens allowed");
        final Protocol protocol = Protocol.All;

        if (tokens.length == 1) {
            return PortRange.singular(protocol, Integer.parseInt(tokens[0]));
        }

        return PortRange.from(protocol, Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]));
    }
}