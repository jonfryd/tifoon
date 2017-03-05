package com.elixlogic.tifoon.domain.model.scanner;

import com.elixlogic.tifoon.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.springframework.util.Assert;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Port extends ReflectionObjectTreeAware implements Serializable {
    private Protocol protocol;
    private int portNumber;

    // don't use auto-generation here, enforce use of setters
    public static Port from(@NonNull final Protocol _protocol, final int _portNumber) {
        final Port port = new Port();
        port.setProtocol(_protocol);
        port.setPortNumber(_portNumber);

        return port;
    }

    public void setPortNumber(final int _portNumber) {
        Assert.isTrue(_portNumber >= 0 && _portNumber <= 65535, String.format("port number %d not within 0-65535 range", _portNumber));
        this.portNumber = _portNumber;
    }

    @Override
    public boolean equals(final Object _o) {
        if (this == _o) {
            return true;
        }

        if (_o == null || getClass() != _o.getClass()) {
            return false;
        }

        final Port port = (Port) _o;

        return new EqualsBuilder()
                .append(portNumber, port.portNumber)
                .append(protocol, port.protocol)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return portNumber + protocol.ordinal() * 100000;
    }
}
