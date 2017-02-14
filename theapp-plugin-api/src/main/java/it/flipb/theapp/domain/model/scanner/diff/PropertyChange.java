package it.flipb.theapp.domain.model.scanner.diff;

import lombok.*;

import javax.annotation.Nullable;
import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Nullable
    private GlobalId globalId;
    @Nullable
    private Type type;
    @Nullable
    private Operation operation;
    @Nullable
    private String property;
    @Nullable
    private String key;
    @Nullable
    private String oldValue;
    @Nullable
    private String newValue;

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
