package it.flipb.theapp.service.scanner.impl;

import it.flipb.theapp.domain.model.scanner.NetworkResult;
import it.flipb.theapp.domain.model.scanner.Port;
import it.flipb.theapp.domain.model.scanner.PortScannerResult;
import it.flipb.theapp.domain.model.scanner.Protocol;
import it.flipb.theapp.domain.model.scanner.diff.Operation;
import it.flipb.theapp.domain.model.scanner.diff.PortScannerDiff;
import it.flipb.theapp.domain.model.scanner.diff.PropertyChange;
import it.flipb.theapp.domain.model.scanner.diff.Type;
import it.flipb.theapp.domain.service.scanner.impl.PortScannerResultDiffServiceImpl;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PortScannerResultDiffServiceImplTest {
    final PortScannerResultDiffServiceImpl portScannerResultDiffService = new PortScannerResultDiffServiceImpl();

    @Test
    public void testSimplestPossibleNoChange() {
        final PortScannerResult portScannerResult = new PortScannerResult(0, 0, true, Collections.EMPTY_LIST);

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult, portScannerResult);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).isEmpty();
    }

    @Test
    public void testComplexNoChange() throws UnknownHostException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.UDP, 132);
        final Port port3 = Port.from(Protocol.STCP, 1234);

        final Map<InetAddress, List<Port>> inetAddressPortMap = new HashMap<>();
        inetAddressPortMap.put(InetAddress.getByName("1.5.3.6"), Arrays.asList(port1, port2));
        inetAddressPortMap.put(InetAddress.getByName("35.74.52.5"), Collections.singletonList(port3));

        final NetworkResult networkResult1 = new NetworkResult("test1", inetAddressPortMap);
        final NetworkResult networkResult2 = new NetworkResult("test2", Collections.EMPTY_MAP);

        final PortScannerResult portScannerResult = new PortScannerResult(0, 0, true, Arrays.asList(networkResult1, networkResult2));

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult, portScannerResult);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).isEmpty();
    }

    @Test
    public void testMissingNetworkResult() throws UnknownHostException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.UDP, 132);
        final Port port3 = Port.from(Protocol.STCP, 1234);

        final Map<InetAddress, List<Port>> inetAddressPortMap = new HashMap<>();
        inetAddressPortMap.put(InetAddress.getByName("1.5.3.6"), Arrays.asList(port1, port2));
        inetAddressPortMap.put(InetAddress.getByName("35.74.52.5"), Collections.singletonList(port3));

        final NetworkResult networkResult1 = new NetworkResult("test1", inetAddressPortMap);
        final NetworkResult networkResult2 = new NetworkResult("test2", Collections.EMPTY_MAP);

        final PortScannerResult portScannerResult1 = new PortScannerResult(0, 0, true, Arrays.asList(networkResult1, networkResult2));
        portScannerResult1.setId(1L);
        final PortScannerResult portScannerResult2 = new PortScannerResult(0, 0, true, Arrays.asList(networkResult2));
        portScannerResult2.setId(2L);

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult1, portScannerResult2);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).hasSize(1);

        final String key = PortScannerResult.class.getCanonicalName();

        assertThat(portScannerDiff.getEntityChangeMap().get(key).getChanges()).hasSize(1);

        final PropertyChange theChange = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(0);

        assertThat(theChange.getGlobalId().getEntityId()).isEqualTo(1L);
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

        final NetworkResult networkResult1 = new NetworkResult("test", inetAddressPortMap1);
        final NetworkResult networkResult2 = new NetworkResult("test", inetAddressPortMap2);

        final PortScannerResult portScannerResult1 = new PortScannerResult(0, 0, true, Collections.singletonList(networkResult1));
        portScannerResult1.setId(1L);
        final PortScannerResult portScannerResult2 = new PortScannerResult(0, 0, true, Collections.singletonList(networkResult2));
        portScannerResult2.setId(2L);

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult1, portScannerResult2);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).hasSize(1);

        final String key = PortScannerResult.class.getCanonicalName();

        assertThat(portScannerDiff.getEntityChangeMap().get(key).getChanges()).hasSize(1);

        final PropertyChange theChange = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(0);

        assertThat(theChange.getGlobalId().getEntityId()).isEqualTo(2L);
        assertThat(theChange.getGlobalId().getSelector()).isEqualTo(key + "/2#networkResults/0/openHosts/0/openPorts/0/port");
        assertThat(theChange.getType()).isEqualTo(Type.OBJECT);
        assertThat(theChange.getOperation()).isEqualTo(Operation.VALUE_MODIFICATION);
        assertThat(theChange.getProperty()).isEqualTo("portNumber");
        assertThat(theChange.getOldValue()).isEqualTo("23");
        assertThat(theChange.getNewValue()).isEqualTo("125");
    }


    @Test
    public void testPortAdded() throws UnknownHostException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.TCP, 523);

        final Map<InetAddress, List<Port>> inetAddressPortMap1 = new HashMap<>();
        inetAddressPortMap1.put(InetAddress.getByName("1.5.3.6"), Collections.singletonList(port1));
        final Map<InetAddress, List<Port>> inetAddressPortMap2 = new HashMap<>();
        inetAddressPortMap2.put(InetAddress.getByName("1.5.3.6"), Arrays.asList(port1, port2));

        final NetworkResult networkResult1 = new NetworkResult("test", inetAddressPortMap1);
        final NetworkResult networkResult2 = new NetworkResult("test", inetAddressPortMap2);

        final PortScannerResult portScannerResult1 = new PortScannerResult(0, 0, true, Collections.singletonList(networkResult1));
        portScannerResult1.setId(1L);
        final PortScannerResult portScannerResult2 = new PortScannerResult(0, 0, true, Collections.singletonList(networkResult2));
        portScannerResult2.setId(2L);

        final PortScannerDiff portScannerDiff = portScannerResultDiffService.diff(portScannerResult1, portScannerResult2);

        assertThat(portScannerDiff).isNotNull();
        assertThat(portScannerDiff.getEntityChangeMap()).hasSize(1);

        System.out.println(portScannerDiff);

        final String key = PortScannerResult.class.getCanonicalName();

        assertThat(portScannerDiff.getEntityChangeMap().get(key).getChanges()).hasSize(4);

        final PropertyChange openPortsCollectionChange = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(0);

        assertThat(openPortsCollectionChange.getGlobalId().getEntityId()).isEqualTo(2L);
        assertThat(openPortsCollectionChange.getGlobalId().getSelector()).isEqualTo(key + "/2#networkResults/0/openHosts/0");
        assertThat(openPortsCollectionChange.getType()).isEqualTo(Type.COLLECTION);
        assertThat(openPortsCollectionChange.getOperation()).isEqualTo(Operation.ADDITION);
        assertThat(openPortsCollectionChange.getProperty()).isEqualTo("openPorts");
        assertThat(openPortsCollectionChange.getKey()).isEqualTo("1");
        assertThat(openPortsCollectionChange.getOldValue()).isNull();
        assertThat(openPortsCollectionChange.getNewValue()).isEqualTo("it.flipb.theapp.domain.model.scanner.NetworkResult/#openHosts/0/openPorts/1");

        final PropertyChange openPortAddition = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(1);

        assertThat(openPortAddition.getGlobalId().getEntityId()).isEqualTo(2L);
        assertThat(openPortAddition.getGlobalId().getSelector()).isEqualTo(key + "/2#networkResults/0/openHosts/0/openPorts/1");
        assertThat(openPortAddition.getType()).isEqualTo(Type.OBJECT);
        assertThat(openPortAddition.getOperation()).isEqualTo(Operation.ADDITION);
        assertThat(openPortAddition.getProperty()).isNull();
        assertThat(openPortAddition.getKey()).isNull();
        assertThat(openPortAddition.getOldValue()).isNull();
        assertThat(openPortAddition.getNewValue()).isNull();

        final PropertyChange portAddition = portScannerDiff.getEntityChangeMap().get(key).getChanges().get(2);

        assertThat(portAddition.getGlobalId().getEntityId()).isEqualTo(2L);
        assertThat(portAddition.getGlobalId().getSelector()).isEqualTo(key + "/2#networkResults/0/openHosts/0/openPorts/1/port");
        assertThat(portAddition.getType()).isEqualTo(Type.OBJECT);
        assertThat(portAddition.getOperation()).isEqualTo(Operation.ADDITION);
        assertThat(portAddition.getProperty()).isNull();
        assertThat(portAddition.getKey()).isNull();
        assertThat(portAddition.getOldValue()).isNull();
        assertThat(portAddition.getNewValue()).isNull();
    }
}
