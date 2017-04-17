package com.elixlogic.tifoon.domain.model.object;

import com.elixlogic.tifoon.domain.model.scanner.*;
import org.assertj.core.api.Java6Assertions;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ReflectionObjectTreeAwareTest {
    @Test
    public void testTracePathFromUnrelatedObjectReturnsNull() {
        final PortScannerResult portScannerResult = new PortScannerResult(UUID.randomUUID().toString(), 0, 0, PortScannerStatus.DONE, Collections.EMPTY_LIST, "", Collections.EMPTY_LIST);

        final NetworkResult unrelatedNetworkResult = new NetworkResult("network1", true, Collections.EMPTY_MAP);

        final List<ObjectTreeAware> path = portScannerResult.traceObjectPath(unrelatedNetworkResult);

        Java6Assertions.assertThat(path).as("the path to an unrelated object must be null").isNull();
    }

    @Test
    public void testTracePathFromEntity() {
        final PortScannerResult portScannerResult = new PortScannerResult(UUID.randomUUID().toString(), 0, 0, PortScannerStatus.DONE, Collections.EMPTY_LIST, "", Collections.EMPTY_LIST);

        final List<ObjectTreeAware> path = portScannerResult.traceObjectPath(portScannerResult.getNetworkResults());

        Java6Assertions.assertThat(path).as("path should only contain the entity itself").containsExactly(portScannerResult);
    }

    @Test
    public void testTracePathFromProtocol() throws UnknownHostException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.UDP, 53);

        final Map<InetAddress, List<Port>> inetAddressPortMap = new HashMap<>();
        inetAddressPortMap.put(InetAddress.getByName("1.5.3.6"), Arrays.asList(port1, port2));

        final NetworkResult networkResult1 = new NetworkResult("network1", true, Collections.EMPTY_MAP);
        final NetworkResult networkResult2 = new NetworkResult("network2", true, Collections.EMPTY_MAP);
        final NetworkResult networkResult3 = new NetworkResult("network3", true, inetAddressPortMap);

        final List<NetworkResult> networkResults = Arrays.asList(networkResult1, networkResult2, networkResult3);

        final PortScannerResult portScannerResult = new PortScannerResult(UUID.randomUUID().toString(), 0, 0, PortScannerStatus.DONE, Collections.EMPTY_LIST, "", networkResults);

        final List<ObjectTreeAware> path = portScannerResult.traceObjectPath(networkResult3.getOpenHosts().get("1.5.3.6").getOpenPorts().values().stream().collect(Collectors.toList()).get(0).getProtocol());

        Java6Assertions.assertThat(path).as("object path from port2 protocol to root").containsExactly(
                port2,
                networkResult3.getOpenHosts().get("1.5.3.6"),
                networkResult3,
                portScannerResult);
    }

    @Test
    public void testTracePathFromHost() throws UnknownHostException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.UDP, 53);

        final Map<InetAddress, List<Port>> inetAddressPortMap = new HashMap<>();
        inetAddressPortMap.put(InetAddress.getByName("1.5.3.6"), Collections.singletonList(port1));
        inetAddressPortMap.put(InetAddress.getByName("3.88.7.5"), Collections.singletonList(port2));

        final NetworkResult networkResult1 = new NetworkResult("network1", true, Collections.EMPTY_MAP);
        final NetworkResult networkResult2 = new NetworkResult("network2", true, inetAddressPortMap);

        final List<NetworkResult> networkResults = Arrays.asList(networkResult1, networkResult2);

        final PortScannerResult portScannerResult = new PortScannerResult(UUID.randomUUID().toString(), 0, 0, PortScannerStatus.DONE, Collections.EMPTY_LIST, "", networkResults);

        final List<ObjectTreeAware> path = portScannerResult.traceObjectPath(networkResult2.getOpenHosts().get("3.88.7.5"));

        Java6Assertions.assertThat(path).as("object path from host 3.88.7.5 to root").containsExactly(
                networkResult2,
                portScannerResult);
    }
}
