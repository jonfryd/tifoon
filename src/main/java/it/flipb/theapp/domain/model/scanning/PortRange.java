package it.flipb.theapp.domain.model.scanning;

import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

/*
 * Immutable
 */
public class PortRange {
    private Port lowPort;
    private Port highPort;

    public static PortRange from(final Port _lowPort,
                                 final Port _highPort) {
        Assert.notNull(_lowPort, "low port cannot be null");
        Assert.notNull(_highPort, "high port cannot be null");

        final PortRange portRange = new PortRange();
        portRange.lowPort = _lowPort;
        portRange.highPort = _highPort;

        return portRange;
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

    @NotNull
    public Port getLowPort() {
        return lowPort;
    }

    @NotNull
    public Port getHighPort() {
        return highPort;
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

    @Override
    public String toString() {
        return "PortRange{" +
                "lowPort=" + lowPort +
                ", highPort=" + highPort +
                '}';
    }
}
