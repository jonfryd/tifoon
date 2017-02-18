package it.flipb.theapp.domain.model.scanner.diff;

import org.junit.Assert;
import org.junit.Test;

public class PropertyChangeTest {
    @Test
    public void testConstructObjectAddition() {
        final GlobalId globalId = new GlobalId(2L, "it.flipb.theapp.domain.model.scanner.PortScannerResult/2#networkResults/1");
        final PropertyChange propertyChange = PropertyChange.addition(globalId, Type.OBJECT, null, null, null);

        Assert.assertEquals(propertyChange.getGlobalId(), globalId);
        Assert.assertEquals(propertyChange.getType(), Type.OBJECT);
        Assert.assertEquals(propertyChange.getOperation(), Operation.ADDITION);
        Assert.assertNull(propertyChange.getKey());
        Assert.assertNull(propertyChange.getProperty());
        Assert.assertNull(propertyChange.getOldValue());
        Assert.assertNull(propertyChange.getNewValue());
    }

    @Test
    public void testConstructObjectRemoval() {
        final GlobalId globalId = new GlobalId(2L, "it.flipb.theapp.domain.model.scanner.PortScannerResult/2#networkResults/1");
        final PropertyChange propertyChange = PropertyChange.removal(globalId, Type.OBJECT, null, null, null);

        Assert.assertEquals(propertyChange.getGlobalId(), globalId);
        Assert.assertEquals(propertyChange.getType(), Type.OBJECT);
        Assert.assertEquals(propertyChange.getOperation(), Operation.REMOVAL);
        Assert.assertNull(propertyChange.getKey());
        Assert.assertNull(propertyChange.getProperty());
        Assert.assertNull(propertyChange.getOldValue());
        Assert.assertNull(propertyChange.getNewValue());
    }

    @Test
    public void testConstructObjectValueModification() {
        final GlobalId globalId = new GlobalId(2L, "it.flipb.theapp.domain.model.scanner.PortScannerResult/2#networkResults/1");
        final PropertyChange propertyChange = PropertyChange.valueModification(globalId, Type.OBJECT, null, null, null, null);

        Assert.assertEquals(propertyChange.getGlobalId(), globalId);
        Assert.assertEquals(propertyChange.getType(), Type.OBJECT);
        Assert.assertEquals(propertyChange.getOperation(), Operation.VALUE_MODIFICATION);
        Assert.assertNull(propertyChange.getKey());
        Assert.assertNull(propertyChange.getProperty());
        Assert.assertNull(propertyChange.getOldValue());
        Assert.assertNull(propertyChange.getNewValue());
    }
}
