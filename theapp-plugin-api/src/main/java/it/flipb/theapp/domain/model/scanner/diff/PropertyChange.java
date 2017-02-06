package it.flipb.theapp.domain.model.scanner.diff;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class PropertyChange {
    private final static Comparator<PropertyChange> BY_GLOBAL_ID = (p1, p2) -> GlobalId.ORDERING.compare(p1.getGlobalId(), p2.getGlobalId());
    private final static Comparator<PropertyChange> BY_TYPE = Comparator.comparing(PropertyChange::getType);
    private final static Comparator<PropertyChange> BY_OPERATION = Comparator.comparing(PropertyChange::getOperation);
    private final static Comparator<PropertyChange> BY_PROPERTY = Comparator.comparing(PropertyChange::getProperty);
    private final static Comparator<PropertyChange> BY_KEY = Comparator.comparing(PropertyChange::getKey);

    public final static Comparator<PropertyChange> ORDERING = BY_GLOBAL_ID
            .thenComparing(BY_TYPE)
            .thenComparing(BY_OPERATION)
            .thenComparing(BY_PROPERTY)
            .thenComparing(BY_KEY);

    @NonNull
    private GlobalId globalId;
    @NonNull
    private Type type;
    @NonNull
    private Operation operation;
    private String property;
    private String key;
    private String oldValue;
    private String newValue;

    public static PropertyChange addition(final GlobalId _globalId,
                                          final Type _type,
                                          final String _property,
                                          final String _key,
                                          final String _newValue)
    {
        final PropertyChange propertyChange = new PropertyChange(_globalId, _type, Operation.ADDITION);
        propertyChange.setProperty(_property);
        propertyChange.setKey(_key);
        propertyChange.setNewValue(_newValue);

        return propertyChange;
    }

    public static PropertyChange removal(final GlobalId _globalId,
                                         final Type _type,
                                         final String _property,
                                         final String _key,
                                         final String _oldValue)
    {
        final PropertyChange propertyChange = new PropertyChange(_globalId, _type, Operation.REMOVAL);
        propertyChange.setProperty(_property);
        propertyChange.setKey(_key);
        propertyChange.setOldValue(_oldValue);

        return propertyChange;
    }

    public static PropertyChange valueModification(final GlobalId _globalId,
                                                   final Type _type,
                                                   final String _property,
                                                   final String _key,
                                                   final String _oldValue,
                                                   final String _newValue)
    {
        final PropertyChange propertyChange = new PropertyChange(_globalId, _type, Operation.VALUE_MODIFICATION);
        propertyChange.setProperty(_property);
        propertyChange.setKey(_key);
        propertyChange.setOldValue(_oldValue);
        propertyChange.setNewValue(_newValue);

        return propertyChange;
    }
}
