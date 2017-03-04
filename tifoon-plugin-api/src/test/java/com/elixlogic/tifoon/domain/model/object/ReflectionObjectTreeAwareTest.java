package com.elixlogic.tifoon.domain.model.object;

import com.elixlogic.tifoon.domain.model.scanner.NetworkResult;
import com.elixlogic.tifoon.domain.model.scanner.Port;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.model.scanner.Protocol;
import org.assertj.core.api.Java6Assertions;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ReflectionObjectTreeAwareTest {
    @Test
    public void testTracePathFromUnrelatedObjectReturnsNull() {
        final PortScannerResult portScannerResult = new PortScannerResult(0, 0, true, Collections.EMPTY_LIST);
        portScannerResult.setId(UUID.randomUUID().toString());

        final NetworkResult unrelatedNetworkResult = new NetworkResult("network1", Collections.EMPTY_MAP);

        final List<ObjectTreeAware> path = portScannerResult.traceObjectPath(unrelatedNetworkResult);

        Java6Assertions.assertThat(path).as("the path to an unrelated object must be null").isNull();
    }

    @Test
    public void testTracePathFromEntity() {
        final PortScannerResult portScannerResult = new PortScannerResult(0, 0, true, Collections.EMPTY_LIST);
        portScannerResult.setId(UUID.randomUUID().toString());

        final List<ObjectTreeAware> path = portScannerResult.traceObjectPath(portScannerResult.getNetworkResults());

        Java6Assertions.assertThat(path).as("path should only contain the entity itself").containsExactly(portScannerResult);
    }

    @Test
    public void testTracePathFromProtocol() throws UnknownHostException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.UDP, 53);

        final Map<InetAddress, List<Port>> inetAddressPortMap = new HashMap<>();
        inetAddressPortMap.put(InetAddress.getByName("1.5.3.6"), Arrays.asList(port1, port2));

        final NetworkResult networkResult1 = new NetworkResult("network1", Collections.EMPTY_MAP);
        final NetworkResult networkResult2 = new NetworkResult("network2", Collections.EMPTY_MAP);
        final NetworkResult networkResult3 = new NetworkResult("network3", inetAddressPortMap);

        final List<NetworkResult> networkResults = Arrays.asList(networkResult1, networkResult2, networkResult3);

        final PortScannerResult portScannerResult = new PortScannerResult(0, 0, true, networkResults);
        portScannerResult.setId(UUID.randomUUID().toString());

        final List<ObjectTreeAware> path = portScannerResult.traceObjectPath(networkResult3.getOpenHosts().get(0).getOpenPorts().get(0).getPort().getProtocol());

        Java6Assertions.assertThat(path).as("object path from port1 protocol to root").containsExactly(
                port1,
                networkResult3.getOpenHosts().get(0).getOpenPorts().get(0),
                networkResult3.getOpenHosts().get(0),
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

        final NetworkResult networkResult1 = new NetworkResult("network1", Collections.EMPTY_MAP);
        final NetworkResult networkResult2 = new NetworkResult("network2", inetAddressPortMap);

        final List<NetworkResult> networkResults = Arrays.asList(networkResult1, networkResult2);

        final PortScannerResult portScannerResult = new PortScannerResult(0, 0, true, networkResults);
        portScannerResult.setId(UUID.randomUUID().toString());

        final List<ObjectTreeAware> path = portScannerResult.traceObjectPath(networkResult2.getOpenHosts().get(1));

        Java6Assertions.assertThat(path).as("object path from host 3.88.7.5 to root").containsExactly(
                networkResult2,
                portScannerResult);
    }
}
