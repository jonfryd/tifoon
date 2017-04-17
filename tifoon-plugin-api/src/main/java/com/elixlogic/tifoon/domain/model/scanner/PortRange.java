package com.elixlogic.tifoon.domain.model.scanner;

import com.elixlogic.tifoon.domain.model.object.ReflectionObjectTreeAware;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE, staticName = "from")
public class PortRange extends ReflectionObjectTreeAware implements Serializable {
    private Port lowPort;
    private Port highPort;

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

    @JsonIgnore
    public boolean isSinglePort() {
        return getLowPort().getPortNumber() == getHighPort().getPortNumber();
    }

    public Protocol toProtocol() {
        return lowPort.getProtocol(); // or highPort - doesn't matter
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
