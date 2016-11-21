package it.flipb.theapp.domain.model.scanning;

import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;

/*
 * Immutable
 */
public class Port {
    private Protocol protocol;
    private int portNumber;

    public static Port from(final Protocol _protocol,
                            final int _portNumber) {
        Assert.notNull(_protocol, "protocol cannot be null");
        Assert.isTrue(_portNumber >= 0 && _portNumber <= 65535, String.format("port number %d not within 0-65535 range", _portNumber));

        final Port port = new Port();
        port.protocol = _protocol;
        port.portNumber = _portNumber;

        return port;
    }

    @NotNull
    public Protocol getProtocol() {
        return protocol;
    }

    public int getPortNumber() {
        return portNumber;
    }

    @Override
    public String toString() {
        return "Port{" +
                "protocol=" + protocol +
                ", portNumber=" + portNumber +
                '}';
    }
}
