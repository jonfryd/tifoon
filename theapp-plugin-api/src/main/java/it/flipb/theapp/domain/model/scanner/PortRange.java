package it.flipb.theapp.domain.model.scanner;

import lombok.NonNull;
import lombok.Value;

/*
 * Immutable
 */
@Value
public class PortRange {
    @NonNull
    private final Port lowPort;
    @NonNull
    private final Port highPort;

    @NonNull
    public static PortRange from(@NonNull final Port _lowPort,
                                 @NonNull final Port _highPort) {
        return new PortRange(_lowPort, _highPort);
    }

    @NonNull
    public static PortRange from(@NonNull final Protocol _protocol,
                                 final int _lowPortNumber,
                                 final int _highPortNumber) {
        return from(Port.from(_protocol, _lowPortNumber), Port.from(_protocol, _highPortNumber));
    }

    @NonNull
    public static PortRange singular(@NonNull final Protocol _protocol,
                                     final int _portNumber) {
        return from(Port.from(_protocol, _portNumber), Port.from(_protocol, _portNumber));
    }

    public boolean isSinglePort() {
        return getLowPort().getPortNumber() == getHighPort().getPortNumber();
    }

    @NonNull
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
