package com.elixlogic.tifoon.service.scanner.impl;

import com.elixlogic.tifoon.domain.model.scanner.*;
import com.elixlogic.tifoon.domain.model.scanner.diff.Operation;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PropertyChange;
import com.elixlogic.tifoon.domain.model.scanner.diff.Type;
import com.elixlogic.tifoon.domain.service.scanner.impl.PortScannerResultDiffServiceImpl;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PortScannerResultDiffServiceImplTest {
    final PortScannerResultDiffServiceImpl portScannerResultDiffService = new PortScannerResultDiffServiceImpl();

    @Test
    public void testSimplestPossibleNoChange() {
        final PortScannerResult portScannerResult = new PortScannerResult(UUID.randomUUID().toString(), 0, 0, PortScannerStatus.DONE, Collections.EMPTY_LIST);

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult, portScannerResult);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).isEmpty();
    }

    @Test
    public void testComplexNoChange() throws UnknownHostException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.UDP, 132);
        final Port port3 = Port.from(Protocol.SCTP, 1234);

        final Map<InetAddress, List<Port>> inetAddressPortMap = new HashMap<>();
        inetAddressPortMap.put(InetAddress.getByName("1.5.3.6"), Arrays.asList(port1, port2));
        inetAddressPortMap.put(InetAddress.getByName("35.74.52.5"), Collections.singletonList(port3));

        final NetworkResult networkResult1 = new NetworkResult("test1", true, inetAddressPortMap);
        final NetworkResult networkResult2 = new NetworkResult("test2", true, Collections.EMPTY_MAP);

        final PortScannerResult portScannerResult = new PortScannerResult(UUID.randomUUID().toString(), 0, 0, PortScannerStatus.DONE, Arrays.asList(networkResult1, networkResult2));

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult, portScannerResult);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).isEmpty();
    }

    @Test
    public void testMissingNetworkResult() throws UnknownHostException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.UDP, 132);
        final Port port3 = Port.from(Protocol.SCTP, 1234);

        final Map<InetAddress, List<Port>> inetAddressPortMap = new HashMap<>();
        inetAddressPortMap.put(InetAddress.getByName("1.5.3.6"), Arrays.asList(port1, port2));
        inetAddressPortMap.put(InetAddress.getByName("35.74.52.5"), Collections.singletonList(port3));

        final NetworkResult networkResult1 = new NetworkResult("test1", true, inetAddressPortMap);
        final NetworkResult networkResult2 = new NetworkResult("test2", true, Collections.EMPTY_MAP);

        final PortScannerResult portScannerResult1 = new PortScannerResult("1", 0, 0, PortScannerStatus.DONE, Arrays.asList(networkResult1, networkResult2));
        final PortScannerResult portScannerResult2 = new PortScannerResult("2", 0, 0, PortScannerStatus.DONE, Arrays.asList(networkResult2));

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult1, portScannerResult2);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).hasSize(1);

        final String key = PortScannerResult.class.getCanonicalName();

        assertThat(portScannerDiff.getEntityChangeMap().get(key).getChanges()).hasSize(1);

        final PropertyChange theChange = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(0);

        assertThat(theChange.getGlobalId().getEntityId()).isEqualTo("1");
        assertThat(theChange.getGlobalId().getSelector()).isEqualTo(key + "/1#networkResults/0");
        assertThat(theChange.getType()).isEqualTo(Type.OBJECT);
        assertThat(theChange.getOperation()).isEqualTo(Operation.REMOVAL);
    }

    @Test
    public void testPortChange() throws UnknownHostException {
        final Port port1a = Port.from(Protocol.TCP, 23);
        final Port port1b = Port.from(Protocol.TCP, 125);

        final Map<InetAddress, List<Port>> inetAddressPortMap1 = new HashMap<>();
        inetAddressPortMap1.put(InetAddress.getByName("1.5.3.6"), Collections.singletonList(port1a));
        final Map<InetAddress, List<Port>> inetAddressPortMap2 = new HashMap<>();
        inetAddressPortMap2.put(InetAddress.getByName("1.5.3.6"), Collections.singletonList(port1b));

        final NetworkResult networkResult1 = new NetworkResult("test", true, inetAddressPortMap1);
        final NetworkResult networkResult2 = new NetworkResult("test", true, inetAddressPortMap2);

        final PortScannerResult portScannerResult1 = new PortScannerResult("1", 0, 0, PortScannerStatus.DONE, Collections.singletonList(networkResult1));
        final PortScannerResult portScannerResult2 = new PortScannerResult("2", 0, 0, PortScannerStatus.DONE, Collections.singletonList(networkResult2));

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult1, portScannerResult2);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).hasSize(1);

        final String key = PortScannerResult.class.getCanonicalName();

        assertThat(portScannerDiff.getEntityChangeMap().get(key).getChanges()).hasSize(4);

        final PropertyChange openPortRemoved = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(0);

        assertThat(openPortRemoved.getGlobalId().getEntityId()).isEqualTo("1");
        assertThat(openPortRemoved.getGlobalId().getSelector()).isEqualTo(key + "/1#networkResults/0/openHosts/1.5.3.6/openPorts/23");
        assertThat(openPortRemoved.getType()).isEqualTo(Type.OBJECT);
        assertThat(openPortRemoved.getOperation()).isEqualTo(Operation.REMOVAL);
        assertThat(openPortRemoved.getProperty()).isNull();
        assertThat(openPortRemoved.getKey()).isNull();
        assertThat(openPortRemoved.getOldValue()).isNull();
        assertThat(openPortRemoved.getNewValue()).isNull();

        final PropertyChange mapAddition = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(1);

        assertThat(mapAddition.getGlobalId().getEntityId()).isEqualTo("2");
        assertThat(mapAddition.getGlobalId().getSelector()).isEqualTo(key + "/2#networkResults/0/openHosts/1.5.3.6");
        assertThat(mapAddition.getType()).isEqualTo(Type.MAP);
        assertThat(mapAddition.getOperation()).isEqualTo(Operation.ADDITION);
        assertThat(mapAddition.getProperty()).isEqualTo("openPorts");
        assertThat(mapAddition.getKey()).isEqualTo("125");
        assertThat(mapAddition.getOldValue()).isNull();
        assertThat(mapAddition.getNewValue()).isEqualTo("com.elixlogic.tifoon.domain.model.scanner.NetworkResult/#openHosts/1.5.3.6/openPorts/125");

        final PropertyChange mapRemoval = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(2);

        assertThat(mapRemoval.getGlobalId().getEntityId()).isEqualTo("2");
        assertThat(mapRemoval.getGlobalId().getSelector()).isEqualTo(key + "/2#networkResults/0/openHosts/1.5.3.6");
        assertThat(mapRemoval.getType()).isEqualTo(Type.MAP);
        assertThat(mapRemoval.getOperation()).isEqualTo(Operation.REMOVAL);
        assertThat(mapRemoval.getProperty()).isEqualTo("openPorts");
        assertThat(mapRemoval.getKey()).isEqualTo("23");
        assertThat(mapRemoval.getOldValue()).isEqualTo("com.elixlogic.tifoon.domain.model.scanner.NetworkResult/#openHosts/1.5.3.6/openPorts/23");
        assertThat(mapRemoval.getNewValue()).isNull();

        final PropertyChange openPortAdded = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(3);

        assertThat(openPortAdded.getGlobalId().getEntityId()).isEqualTo("2");
        assertThat(openPortAdded.getGlobalId().getSelector()).isEqualTo(key + "/2#networkResults/0/openHosts/1.5.3.6/openPorts/125");
        assertThat(openPortAdded.getType()).isEqualTo(Type.OBJECT);
        assertThat(openPortAdded.getOperation()).isEqualTo(Operation.ADDITION);
        assertThat(openPortAdded.getProperty()).isNull();
        assertThat(openPortAdded.getKey()).isNull();
        assertThat(openPortAdded.getOldValue()).isNull();
        assertThat(openPortAdded.getNewValue()).isNull();
    }

    @Test
    public void testPortAdded() throws UnknownHostException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.TCP, 523);

        final Map<InetAddress, List<Port>> inetAddressPortMap1 = new HashMap<>();
        inetAddressPortMap1.put(InetAddress.getByName("1.5.3.6"), Collections.singletonList(port1));
        final Map<InetAddress, List<Port>> inetAddressPortMap2 = new HashMap<>();
        inetAddressPortMap2.put(InetAddress.getByName("1.5.3.6"), Arrays.asList(port2, port1));

        final NetworkResult networkResult1 = new NetworkResult("test", true, inetAddressPortMap1);
        final NetworkResult networkResult2 = new NetworkResult("test", true, inetAddressPortMap2);

        final PortScannerResult portScannerResult1 = new PortScannerResult("1", 0, 0, PortScannerStatus.DONE, Collections.singletonList(networkResult1));
        final PortScannerResult portScannerResult2 = new PortScannerResult("2", 0, 0, PortScannerStatus.DONE, Collections.singletonList(networkResult2));

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult1, portScannerResult2);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).hasSize(1);

        final String key = PortScannerResult.class.getCanonicalName();

        assertThat(portScannerDiff.getEntityChangeMap().get(key).getChanges()).hasSize(2);

        final PropertyChange openPortsMapChange = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(0);

        assertThat(openPortsMapChange.getGlobalId().getEntityId()).isEqualTo("2");
        assertThat(openPortsMapChange.getGlobalId().getSelector()).isEqualTo(key + "/2#networkResults/0/openHosts/1.5.3.6");
        assertThat(openPortsMapChange.getType()).isEqualTo(Type.MAP);
        assertThat(openPortsMapChange.getOperation()).isEqualTo(Operation.ADDITION);
        assertThat(openPortsMapChange.getProperty()).isEqualTo("openPorts");
        assertThat(openPortsMapChange.getKey()).isEqualTo("523");
        assertThat(openPortsMapChange.getOldValue()).isNull();
        assertThat(openPortsMapChange.getNewValue()).isEqualTo("com.elixlogic.tifoon.domain.model.scanner.NetworkResult/#openHosts/1.5.3.6/openPorts/523");

        final PropertyChange openPortAddition = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(1);

        assertThat(openPortAddition.getGlobalId().getEntityId()).isEqualTo("2");
        assertThat(openPortAddition.getGlobalId().getSelector()).isEqualTo(key + "/2#networkResults/0/openHosts/1.5.3.6/openPorts/523");
        assertThat(openPortAddition.getType()).isEqualTo(Type.OBJECT);
        assertThat(openPortAddition.getOperation()).isEqualTo(Operation.ADDITION);
        assertThat(openPortAddition.getProperty()).isNull();
        assertThat(openPortAddition.getKey()).isNull();
        assertThat(openPortAddition.getOldValue()).isNull();
        assertThat(openPortAddition.getNewValue()).isNull();
    }
}
