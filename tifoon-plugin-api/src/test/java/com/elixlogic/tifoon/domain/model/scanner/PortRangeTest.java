package com.elixlogic.tifoon.domain.model.scanner;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class PortRangeTest {
    @Test
    public void canConstructSinglePortRange() {
        final PortRange portRange = PortRange.singular(Protocol.TCP, 139);

        assertThat(portRange.getLowPort().getPortNumber()).as("lowPort").isEqualTo(139);
        assertThat(portRange.getHighPort().getPortNumber()).as("highPort").isEqualTo(139);
        assertThat(portRange.isSinglePort()).as("should be single port").isTrue();
        assertThat(portRange.toSingleOrIntervalString()).as("interval string incorrect").isEqualTo("139");
    }

    @Test(expected = NullPointerException.class)
    public void throwsWhenConstructingSinglePortWithNullProtocol() {
        PortRange.singular(null, 40);
    }

    @Test
    public void canConstructProperPortRange() {
        final PortRange portRange = PortRange.from(Protocol.UDP, 20, 25);

        assertThat(portRange.getLowPort().getPortNumber()).as("lowPort").isEqualTo(20);
        assertThat(portRange.getHighPort().getPortNumber()).as("highPort").isEqualTo(25);
        assertThat(portRange.isSinglePort()).as("should be single port").isFalse();
        assertThat(portRange.toSingleOrIntervalString()).as("interval string incorrect").isEqualTo("20-25");
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
