package it.flipb.theapp.domain.model.scanner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PortScannerResultTest {
    private PortScannerResult portScannerResult;

    @Before
    public void before() {
        portScannerResult = new PortScannerResult();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void initiallyHasEmptyUnmodifiableListInternally() {
        Assert.assertFalse("should not have results", portScannerResult.hasResults());

        // test this is an unmodifiable list, by invoking an operation that causes potential mutation
        // and thus triggers an UnsupportedOperationException
        portScannerResult.getNetworkResults().clear();
    }

    @Test
    public void calculatesExecutionTimeCorrectly() {
        final long startTimestamp = System.currentTimeMillis();

        portScannerResult.setBeganAt(startTimestamp);
        portScannerResult.setEndedAt(startTimestamp + 2345);

        Assert.assertEquals("execution time incorrect", portScannerResult.calcExecutionTimeMillis(), 2345);
    }

    @Test
    public void returnsValidMapByNetworkId() {
        final NetworkResult networkResult1 = new NetworkResult("network1", Collections.EMPTY_MAP);
        final NetworkResult networkResult2 = new NetworkResult("network2", Collections.EMPTY_MAP);
        final NetworkResult networkResult3 = new NetworkResult("network3", Collections.EMPTY_MAP);

        final List<NetworkResult> networkResults = Arrays.asList(networkResult1, networkResult2, networkResult3);

        portScannerResult.setNetworkResults(networkResults);

        Assert.assertEquals("no. results incorrect", portScannerResult.numberOfResults(), 3);

        Map<String, NetworkResult> networkResultMap = portScannerResult.getNetworkResultMapByNetworkId();

        Assert.assertEquals("map size incorrect", networkResultMap.size(), 3);
        Assert.assertEquals("should contain network1", networkResultMap.get("network1"), networkResult1);
        Assert.assertEquals("should contain network2", networkResultMap.get("network2"), networkResult2);
        Assert.assertEquals("should contain network3", networkResultMap.get("network3"), networkResult3);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void providingNullNetworkResultsIsAnEmptyUnmodifiableListInternally() {
        portScannerResult.setNetworkResults(null);

        Assert.assertFalse("should not have results", portScannerResult.hasResults());

        // test this is an unmodifiable list, by invoking an operation that causes potential mutation
        // and thus triggers an UnsupportedOperationException
        portScannerResult.getNetworkResults().clear();
    }
}
