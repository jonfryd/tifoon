package it.flipb.theapp.domain.model.scanner;

import org.junit.Assert;
import org.junit.Test;

public class PortRangeTest {
    @Test
    public void canConstructSinglePortRange() {
        final PortRange portRange = PortRange.singular(Protocol.TCP, 139);

        Assert.assertEquals("Low port incorrect", 139, portRange.getLowPort().getPortNumber());
        Assert.assertEquals("High port incorrect", 139, portRange.getHighPort().getPortNumber());
        Assert.assertTrue("Should be single port", portRange.isSinglePort());
        Assert.assertEquals("Interval string incorrect", "139", portRange.toSingleOrIntervalString());
    }

    @Test(expected = NullPointerException.class)
    public void throwsWhenConstructingSinglePortWithNullProtocol() {
        PortRange.singular(null, 40);
    }

    @Test
    public void canConstructProperPortRange() {
        final PortRange portRange = PortRange.from(Protocol.UDP, 20, 25);

        Assert.assertEquals("Low port incorrect", 20, portRange.getLowPort().getPortNumber());
        Assert.assertEquals("High port incorrect", 25, portRange.getHighPort().getPortNumber());
        Assert.assertFalse("Should be not be single port", portRange.isSinglePort());
        Assert.assertEquals("Interval string incorrect", "20-25", portRange.toSingleOrIntervalString());
    }

    @Test(expected = NullPointerException.class)
    public void throwsWhenConstructingRangeWithNullProtocol() {
        PortRange.from(null, 40, 42);
    }

    @Test(expected = IllegalArgumentException.class)
    public void throwsWhenConstructingInvalidRange() {
        PortRange.from(Protocol.STCP, 500, 450);
    }
}
