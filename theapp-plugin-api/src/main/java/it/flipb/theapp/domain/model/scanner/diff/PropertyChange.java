package it.flipb.theapp.domain.model.scanner.diff;

import lombok.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.Comparator;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertyChange implements Serializable {
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

    private GlobalId globalId;
    private Type type;
    private Operation operation;
    private String property;
    private String key;
    private String oldValue;
    private String newValue;

    private PropertyChange(final GlobalId _globalId,
                          final Type _type,
                          final Operation _operation,
                          @Nullable final String _property,
                          @Nullable final String _key,
                          @Nullable final String _oldValue,
                          @Nullable final String _newValue) {
        globalId = _globalId;
        type = _type;
        operation = _operation;
        property = _property;
        key = _key;
        oldValue = _oldValue;
        newValue = _newValue;
    }

    public static PropertyChange addition(final GlobalId _globalId,
                                          final Type _type,
                                          @Nullable final String _property,
                                          @Nullable final String _key,
                                          @Nullable final String _newValue)
    {
        return new PropertyChange(_globalId, _type, Operation.ADDITION, _property, _key, null, _newValue);
    }

    public static PropertyChange removal(final GlobalId _globalId,
                                         final Type _type,
                                         @Nullable final String _property,
                                         @Nullable final String _key,
                                         @Nullable final String _oldValue)
    {
        return new PropertyChange(_globalId, _type, Operation.REMOVAL, _property, _key, _oldValue, null);
    }

    public static PropertyChange valueModification(final GlobalId _globalId,
                                                   final Type _type,
                                                   @Nullable final String _property,
                                                   @Nullable final String _key,
                                                   @Nullable final String _oldValue,
                                                   @Nullable final String _newValue)
    {
        return new PropertyChange(_globalId, _type, Operation.VALUE_MODIFICATION, _property, _key, _oldValue, _newValue);
    }
}
