package it.flipb.theapp.domain.model.scanner;

import org.junit.Assert;
import org.junit.Test;

public class PortTest {
    @Test
    public void canConstructValidPort() {
        final Port port = Port.from(Protocol.IP, 23);

        Assert.assertEquals("Protocol incorrect", Protocol.IP, port.getProtocol());
        Assert.assertEquals("Port number incorrect", 23, port.getPortNumber());
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
