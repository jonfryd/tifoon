package it.flipb.theapp.domain.model.scanner;

import lombok.Data;
import lombok.Value;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

/*
 * Immutable
 */
@Value
public class PortRange {
    private final Port lowPort;
    private final Port highPort;

    public static PortRange from(final Port _lowPort,
                                 final Port _highPort) {
        Assert.notNull(_lowPort, "low port cannot be null");
        Assert.notNull(_highPort, "high port cannot be null");

        return new PortRange(_lowPort, _highPort);
    }

    public static PortRange from(final Protocol _protocol,
                                 final int _lowPortNumber,
                                 final int _highPortNumber) {
        return from(Port.from(_protocol, _lowPortNumber), Port.from(_protocol, _highPortNumber));
    }


    public static PortRange singular(final Protocol _protocol,
                                     final int _portNumber) {
        return from(Port.from(_protocol, _portNumber), Port.from(_protocol, _portNumber));
    }

    public boolean isSinglePort() {
        return getLowPort().getPortNumber() == getHighPort().getPortNumber();
    }

    @NotNull
    public String toSingleOrIntervalString() {
        // is single?
        if (isSinglePort()) {
            return String.valueOf(getLowPort().getPortNumber());
        }

        // return as internal
        return String.valueOf(getLowPort().getPortNumber())
                + "-"
                + String.valueOf(getHighPort().getPortNumber());
    }
}
