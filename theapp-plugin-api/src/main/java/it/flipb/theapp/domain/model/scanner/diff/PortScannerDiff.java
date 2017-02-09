package it.flipb.theapp.domain.model.scanner.diff;

import it.flipb.theapp.domain.model.object.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PortScannerDiff {
    private final static Function<Class, String> CLASS_TO_MAP_KEY = Class::getCanonicalName;

    @Data
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class GenericChangeList {
        @NonNull
        private List<PropertyChange> changes;
    }

    @NonNull
    private Map<String, GenericChangeList> entityChangeMap;

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
                                                    final String _pathRegExPattern,
                                                    final String _property,
                                                    final String _key,
                                                    final Type _type,
                                                    final Operation _operation)
    {
        final GenericChangeList genericChangeList = entityChangeMap.get(CLASS_TO_MAP_KEY.apply(_owner));

        return genericChangeList == null ?
                null :
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
