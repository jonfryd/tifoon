package com.elixlogic.tifoon.domain.model.scanner.diff;

import com.elixlogic.tifoon.domain.model.object.BaseEntity;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import org.assertj.core.api.Java6Assertions;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PortScannerDiffTest {
    private PropertyChange propertyChange0;
    private PropertyChange propertyChange1;
    private PropertyChange propertyChange2;
    private PropertyChange propertyChange3;
    private PropertyChange propertyChange4;
    private PropertyChange propertyChange5;

    private PortScannerDiff portScannerDiff;

    @Before
    public void before() {
        final GlobalId globalId0 = new GlobalId(2L, "PortScannerResult/2#networkResults/1");
        propertyChange0 = PropertyChange.valueModification(globalId0, Type.OBJECT, null, null, null, null);

        final GlobalId globalId1 = new GlobalId(4L, "PortScannerResult/4#networkResults/2");
        propertyChange1 = PropertyChange.addition(globalId1, Type.COLLECTION, "openHosts", "0", "NetworkResult/#openHosts/0");

        final GlobalId globalId2 = new GlobalId(2L, "PortScannerResult/2#networkResults/1");
        propertyChange2 = PropertyChange.addition(globalId2, Type.COLLECTION, "openHosts", "0", "NetworkResult/#openHosts/0");

        final GlobalId globalId3 = new GlobalId(1L, "PortScannerResult/1#networkResults/0");
        propertyChange3 = PropertyChange.addition(globalId3, Type.COLLECTION, "openHosts", "0", "NetworkResult/#openHosts/0");

        final GlobalId globalId4 = new GlobalId(2L, "PortScannerResult/2#networkResults/1");
        propertyChange4 = PropertyChange.removal(globalId4, Type.OBJECT, null, null, null);

        final GlobalId globalId5 = new GlobalId(1L, "PortScannerResult/2#networkResults/0/fake");
        propertyChange5 = PropertyChange.valueModification(globalId5, Type.OBJECT, "someProperty", null, "1", "2");

        final Map<Class<? extends BaseEntity>, Collection<PropertyChange>> classPropertyChangesMap = new HashMap<>();
        classPropertyChangesMap.put(PortScannerResult.class, Arrays.asList(propertyChange0, propertyChange1, propertyChange2, propertyChange3, propertyChange4, propertyChange5));

        portScannerDiff = PortScannerDiff.from(classPropertyChangesMap);
    }

    @Test
    public void testPropertyChangesAreOrdered() {
        final String key = PortScannerResult.class.getCanonicalName();

        assertThat(portScannerDiff.getEntityChangeMap().keySet()).as("keyset is correct").containsExactly(key);
        assertThat(portScannerDiff.getEntityChangeMap().get(key).getChanges()).as("changes list is correct")
                .containsExactly(propertyChange3, propertyChange5, propertyChange4, propertyChange0, propertyChange2, propertyChange1);
    }

    @Test
    public void testFindInPath() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults.*", null, null, null, null);
        final String key = PortScannerResult.class.getCanonicalName();

        Java6Assertions.assertThat(propertyChanges).hasSize(6).containsAll(portScannerDiff.getEntityChangeMap().get(key).getChanges());
    }

    @Test
    public void testFindExactInPath() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, "networkResults/1", null, null, null, null);

        Java6Assertions.assertThat(propertyChanges).containsExactly(propertyChange4, propertyChange0, propertyChange2);
    }

    @Test
    public void testNoMatchInPath() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, "foobar", null, null, null, null);

        Java6Assertions.assertThat(propertyChanges).isEmpty();
    }

    @Test
    public void testFindProperty() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, null, "someProperty", null, null, null);

        Java6Assertions.assertThat(propertyChanges).containsExactly(propertyChange5);
    }

    @Test
    public void testCannotFindProperty() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, null, "someUnkownnProperty", null, null, null);

        Java6Assertions.assertThat(propertyChanges).isEmpty();
    }

    @Test
    public void testFindKey() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, null, null, "0", null, null);

        Java6Assertions.assertThat(propertyChanges).containsExactly(propertyChange3, propertyChange2, propertyChange1);
    }

    @Test
    public void testCannotFindKey() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, null, null, "someUnknownKey", null, null);

        Java6Assertions.assertThat(propertyChanges).isEmpty();
    }

    @Test
    public void testFindType() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, null, null, null, Type.OBJECT, null);

        Java6Assertions.assertThat(propertyChanges).containsExactly(propertyChange5, propertyChange4, propertyChange0);
    }

    @Test
    public void testCannotFindType() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, null, null, null, Type.ARRAY, null);

        Java6Assertions.assertThat(propertyChanges).isEmpty();
    }

    @Test
    public void testFindOperation() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, null, null, null, null, Operation.VALUE_MODIFICATION);

        Java6Assertions.assertThat(propertyChanges).containsExactly(propertyChange5, propertyChange0);
    }

    @Test
    public void testFindCombinationOfCollectionAndAddition() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, null, null, null, Type.COLLECTION, Operation.ADDITION);

        Java6Assertions.assertThat(propertyChanges).containsExactly(propertyChange3, propertyChange2, propertyChange1);
    }

    @Test
    public void testCannotFindCombinationOfCollectionAndRemoval() {
        final List<PropertyChange> propertyChanges = portScannerDiff.findPropertyChanges(PortScannerResult.class, null, null, null, Type.COLLECTION, Operation.REMOVAL);

        Java6Assertions.assertThat(propertyChanges).isEmpty();
    }
}
