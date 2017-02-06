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
    private final static Function<Class, String> CLASS_TO_MAP_KEY = _class -> _class.getCanonicalName();

    @Data
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class GenericChangeList {
        @NonNull
        List<PropertyChange> changes;
    }

    @NonNull
    Map<String, GenericChangeList> entityChangeMap;

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
                                                    final String _path,
                                                    final String _property,
                                                    final String _key)
    {
        final GenericChangeList genericChangeList = entityChangeMap.get(CLASS_TO_MAP_KEY.apply(_owner));
        List<PropertyChange> propertyChanges = genericChangeList != null ? genericChangeList.getChanges() : null;

        if (propertyChanges != null)
        {
            propertyChanges = genericChangeList
                    .getChanges()
                    .stream()
                    .filter(c -> _path == null || c.getGlobalId().getSelector().contains("#".concat(_path)))
                    .filter(c -> _property == null || _property.equals(c.getProperty()))
                    .filter(c -> _key == null || _key.equals(c.getKey()))
                    .collect(Collectors.toList());
        }

        return propertyChanges;
    }
}
