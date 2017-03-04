package com.elixlogic.tifoon.domain.model.scanner;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class PortTest {
    @Test
    public void canConstructValidPort() {
        final Port port = Port.from(Protocol.IP, 23);

        assertThat(port.getProtocol()).as("protocol").isEqualTo(Protocol.IP);
        assertThat(port.getPortNumber()).as("portNumber").isEqualTo(23);
    }

    @Test(expected = NullPointerException.class)
    public void throwsWhenPassingNullProtocol() {
        Port.from(null, 20);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenPassingTooLowPortNumber() {
        Port.from(Protocol.TCP, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenPassingTooHighPortNumber() {
        Port.from(Protocol.TCP, 65536);
    }
}
