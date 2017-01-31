package it.flipb.theapp.domain.model.scanner.diff;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class PortScannerDiff {
    @Data
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class GlobalIdList {
        @NonNull
        List<GlobalId> ids;
    }
    @Data
    @NoArgsConstructor
    @RequiredArgsConstructor
    public static class GenericChangeList {
        @NonNull
        List<GenericChange> changes;
    }

    @NonNull
    Map<String, GlobalIdList> objectsRemovedMap;
    @NonNull
    Map<String, GlobalIdList> objectsAddedMap;
    @NonNull
    Map<String, GenericChangeList> objectsChangedMap;
    // container changes (added, removed, changed)
    // map changes (added, removed, changed)

    public static PortScannerDiff from(final Map<String, Collection<GlobalId>> _objectsRemovedMap,
                                       final Map<String, Collection<GlobalId>> _objectsAddedMap,
                                       final Map<String, Collection<GenericChange>> _objectsChangedMap) {
        final PortScannerDiff portScannerDiff = new PortScannerDiff();

        portScannerDiff.setObjectsRemovedMap(_objectsRemovedMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new GlobalIdList(new ArrayList<>(e.getValue())))));
        portScannerDiff.setObjectsAddedMap(_objectsAddedMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new GlobalIdList(new ArrayList<>(e.getValue())))));
        portScannerDiff.setObjectsChangedMap(_objectsChangedMap.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new GenericChangeList(new ArrayList<>(e.getValue())))));

        return portScannerDiff;
    }
}
