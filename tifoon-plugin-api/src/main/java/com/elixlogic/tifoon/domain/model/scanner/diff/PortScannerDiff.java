package com.elixlogic.tifoon.domain.model.scanner.diff;

import com.elixlogic.tifoon.domain.model.object.BaseEntity;
import lombok.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PortScannerDiff implements Serializable {
    private final static Function<Class, String> CLASS_TO_MAP_KEY = Class::getCanonicalName;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenericChangeList {
        private List<PropertyChange> changes;
    }

    @Nonnull
    private Map<String, GenericChangeList> entityChangeMap = Collections.unmodifiableMap(new HashMap<>());

    public static PortScannerDiff from(@NonNull final Map<Class<? extends BaseEntity>, Collection<PropertyChange>> _entityChangeMap) {
        final PortScannerDiff portScannerDiff = new PortScannerDiff();

        portScannerDiff.setEntityChangeMap(_entityChangeMap.entrySet()
                .stream()
                .collect(Collectors.toMap(k -> CLASS_TO_MAP_KEY.apply(k.getKey()), e -> {
                    final List<PropertyChange> sortedList = new ArrayList<>(e.getValue())
                            .stream()
                            .sorted(PropertyChange.ORDERING)
                            .collect(Collectors.toList());

                    return new GenericChangeList(sortedList);
                })));

        return portScannerDiff;
    }

    public List<PropertyChange> findPropertyChanges(@NonNull final Class<? extends BaseEntity> _owner,
                                                    @Nullable final String _pathRegExPattern,
                                                    @Nullable final String _property,
                                                    @Nullable final String _key,
                                                    @Nullable final Type _type,
                                                    @Nullable final Operation _operation)
    {
        final GenericChangeList genericChangeList = entityChangeMap.get(CLASS_TO_MAP_KEY.apply(_owner));

        return genericChangeList == null ?
                new ArrayList<>() :
                genericChangeList.getChanges()
                        .stream()
                        .filter(c -> _pathRegExPattern == null || c.getGlobalId().getSelector().matches(".*#".concat(_pathRegExPattern).concat("$")))
                        .filter(c -> _property == null || _property.equals(c.getProperty()))
                        .filter(c -> _key == null || _key.equals(c.getKey()))
                        .filter(c -> _type == null || _type.equals(c.getType()))
                        .filter(c -> _operation == null || _operation.equals(c.getOperation()))
                        .collect(Collectors.toList());
    }
}
