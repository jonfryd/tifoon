package it.flipb.theapp.domain.model.scanner;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

public class PortScannerResultTest {
    private PortScannerResult portScannerResult;

    @Before
    public void before() {
        portScannerResult = new PortScannerResult();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void initiallyHasEmptyUnmodifiableListInternally() {
        assertThat(portScannerResult.hasResults()).as("should not have results").isFalse();

        // test this is an unmodifiable list, by invoking an operation that causes potential mutation
        // and thus triggers an UnsupportedOperationException
        portScannerResult.getNetworkResults().clear();
    }

    @Test
    public void calculatesExecutionTimeCorrectly() {
        final long startTimestamp = System.currentTimeMillis();

        portScannerResult.setBeganAt(startTimestamp);
        portScannerResult.setEndedAt(startTimestamp + 2345);

        assertThat(portScannerResult.calcExecutionTimeMillis()).as("execution time").isEqualTo(2345);
    }

    @Test
    public void returnsValidMapByNetworkId() {
        final NetworkResult networkResult1 = new NetworkResult("network1", Collections.EMPTY_MAP);
        final NetworkResult networkResult2 = new NetworkResult("network2", Collections.EMPTY_MAP);
        final NetworkResult networkResult3 = new NetworkResult("network3", Collections.EMPTY_MAP);

        final List<NetworkResult> networkResults = Arrays.asList(networkResult1, networkResult2, networkResult3);

        portScannerResult.setNetworkResults(networkResults);

        assertThat(portScannerResult.numberOfResults()).as("number of results").isEqualTo(3);

        Map<String, NetworkResult> networkResultMap = portScannerResult.mapNetworkResultsByNetworkId();

        assertThat(networkResultMap.size()).as("map size incorrect").isEqualTo(3);
        assertThat(networkResultMap.values()).as("should contain network1, network2, network3").contains(networkResult1, networkResult2, networkResult3);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void providingNullNetworkResultsIsAnEmptyUnmodifiableListInternally() {
        portScannerResult.setNetworkResults(null);

        assertThat(portScannerResult.hasResults()).as("should not have results").isFalse();

        // test this is an unmodifiable list, by invoking an operation that causes potential mutation
        // and thus triggers an UnsupportedOperationException
        portScannerResult.getNetworkResults().clear();
    }
}
