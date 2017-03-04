package com.elixlogic.tifoon.domain.model.scanner;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, staticName = "from")
public class PortRange {
    @Nonnull
    private final Port lowPort;
    @Nonnull
    private final Port highPort;

    @Nonnull
    public static PortRange from(@NonNull final Protocol _protocol,
                                 final int _lowPortNumber,
                                 final int _highPortNumber) {
        Assert.isTrue(_lowPortNumber <= _highPortNumber, "Low port number must be <= high port number");

        return from(Port.from(_protocol, _lowPortNumber), Port.from(_protocol, _highPortNumber));
    }

    @Nonnull
    public static PortRange singular(@NonNull final Protocol _protocol,
                                     final int _portNumber) {
        return from(Port.from(_protocol, _portNumber), Port.from(_protocol, _portNumber));
    }

    public boolean isSinglePort() {
        return getLowPort().getPortNumber() == getHighPort().getPortNumber();
    }

    @Nonnull
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
