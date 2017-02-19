package it.flipb.theapp.domain.model.scanner.diff;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PropertyChangeTest {
    @Test
    public void testConstructObjectAddition() {
        final GlobalId globalId = new GlobalId(2L, "it.flipb.theapp.domain.model.scanner.PortScannerResult/2#networkResults/1");
        final PropertyChange propertyChange = PropertyChange.addition(globalId, Type.OBJECT, null, null, null);

        assertThat(propertyChange.getGlobalId()).as("globalId").isEqualTo(globalId);
        assertThat(propertyChange.getType()).as("type").isEqualTo(Type.OBJECT);
        assertThat(propertyChange.getOperation()).as("operation").isEqualTo(Operation.ADDITION);
        assertThat(propertyChange.getProperty()).as("property").isNull();
        assertThat(propertyChange.getKey()).as("key").isNull();
        assertThat(propertyChange.getOldValue()).as("oldValue").isNull();
        assertThat(propertyChange.getNewValue()).as("newValue").isNull();
    }

    @Test
    public void testConstructObjectRemoval() {
        final GlobalId globalId = new GlobalId(2L, "it.flipb.theapp.domain.model.scanner.PortScannerResult/2#networkResults/1");
        final PropertyChange propertyChange = PropertyChange.removal(globalId, Type.OBJECT, null, null, null);

        assertThat(propertyChange.getGlobalId()).as("globalId").isEqualTo(globalId);
        assertThat(propertyChange.getType()).as("type").isEqualTo(Type.OBJECT);
        assertThat(propertyChange.getOperation()).as("operation").isEqualTo(Operation.REMOVAL);
        assertThat(propertyChange.getProperty()).as("property").isNull();
        assertThat(propertyChange.getKey()).as("key").isNull();
        assertThat(propertyChange.getOldValue()).as("oldValue").isNull();
        assertThat(propertyChange.getNewValue()).as("newValue").isNull();
    }

    @Test
    public void testConstructObjectValueModification() {
        final GlobalId globalId = new GlobalId(2L, "it.flipb.theapp.domain.model.scanner.PortScannerResult/2#networkResults/1");
        final PropertyChange propertyChange = PropertyChange.valueModification(globalId, Type.OBJECT, null, null, null, null);

        assertThat(propertyChange.getGlobalId()).as("globalId").isEqualTo(globalId);
        assertThat(propertyChange.getType()).as("type").isEqualTo(Type.OBJECT);
        assertThat(propertyChange.getOperation()).as("operation").isEqualTo(Operation.VALUE_MODIFICATION);
        assertThat(propertyChange.getProperty()).as("property").isNull();
        assertThat(propertyChange.getKey()).as("key").isNull();
        assertThat(propertyChange.getOldValue()).as("oldValue").isNull();
        assertThat(propertyChange.getNewValue()).as("newValue").isNull();
    }

    @Test
    public void testConstructCollectionAddition() {
        final GlobalId globalId = new GlobalId(2L, "it.flipb.theapp.domain.model.scanner.PortScannerResult/2#networkResults/1");
        final PropertyChange propertyChange = PropertyChange.addition(globalId, Type.COLLECTION, "openHosts", "0", "it.flipb.theapp.domain.model.scanner.NetworkResult/#openHosts/0");

        assertThat(propertyChange.getGlobalId()).as("globalId").isEqualTo(globalId);
        assertThat(propertyChange.getType()).as("type").isEqualTo(Type.COLLECTION);
        assertThat(propertyChange.getOperation()).as("operation").isEqualTo(Operation.ADDITION);
        assertThat(propertyChange.getProperty()).as("property").isEqualTo("openHosts");
        assertThat(propertyChange.getKey()).as("key").isEqualTo("0");
        assertThat(propertyChange.getOldValue()).as("oldValue").isNull();
        assertThat(propertyChange.getNewValue()).as("newValue").isEqualTo("it.flipb.theapp.domain.model.scanner.NetworkResult/#openHosts/0");
    }

    @Test
    public void testConstructCollectionRemoval() {
        final GlobalId globalId = new GlobalId(2L, "it.flipb.theapp.domain.model.scanner.PortScannerResult/2#networkResults/1");
        final PropertyChange propertyChange = PropertyChange.removal(globalId, Type.COLLECTION, "openHosts", "0", "it.flipb.theapp.domain.model.scanner.NetworkResult/#openHosts/0");

        assertThat(propertyChange.getGlobalId()).as("globalId").isEqualTo(globalId);
        assertThat(propertyChange.getType()).as("type").isEqualTo(Type.COLLECTION);
        assertThat(propertyChange.getOperation()).as("operation").isEqualTo(Operation.REMOVAL);
        assertThat(propertyChange.getProperty()).as("property").isEqualTo("openHosts");
        assertThat(propertyChange.getKey()).as("key").isEqualTo("0");
        assertThat(propertyChange.getOldValue()).as("oldValue").isEqualTo("it.flipb.theapp.domain.model.scanner.NetworkResult/#openHosts/0");
        assertThat(propertyChange.getNewValue()).as("newValue").isNull();
    }

    @Test
    public void testConstructCollectionValueModification() {
        final GlobalId globalId = new GlobalId(2L, "it.flipb.theapp.domain.model.scanner.PortScannerResult/2#networkResults/1");
        final PropertyChange propertyChange = PropertyChange.valueModification(globalId, Type.COLLECTION, "openHosts", "0",
                "it.flipb.theapp.domain.model.scanner.NetworkResult/#openHosts/0",
                "it.flipb.theapp.domain.model.scanner.NetworkResult/#openHosts/1");

        assertThat(propertyChange.getGlobalId()).as("globalId").isEqualTo(globalId);
        assertThat(propertyChange.getType()).as("type").isEqualTo(Type.COLLECTION);
        assertThat(propertyChange.getOperation()).as("operation").isEqualTo(Operation.VALUE_MODIFICATION);
        assertThat(propertyChange.getProperty()).as("property").isEqualTo("openHosts");
        assertThat(propertyChange.getKey()).as("key").isEqualTo("0");
        assertThat(propertyChange.getOldValue()).as("oldValue").isEqualTo("it.flipb.theapp.domain.model.scanner.NetworkResult/#openHosts/0");
        assertThat(propertyChange.getNewValue()).as("newValue").isEqualTo("it.flipb.theapp.domain.model.scanner.NetworkResult/#openHosts/1");
    }

}
