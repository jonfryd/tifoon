package it.flipb.theapp.domain.service.scanner.impl;

import com.google.common.collect.*;
import it.flipb.theapp.domain.model.object.BaseEntity;
import it.flipb.theapp.domain.model.object.ObjectTreeAware;
import it.flipb.theapp.domain.model.scanner.*;
import it.flipb.theapp.domain.model.scanner.diff.GlobalId;
import it.flipb.theapp.domain.model.scanner.diff.PortScannerDiff;
import it.flipb.theapp.domain.model.scanner.diff.PropertyChange;
import it.flipb.theapp.domain.model.scanner.diff.Type;
import it.flipb.theapp.domain.service.scanner.PortScannerResultDiffService;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.diff.changetype.map.*;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.*;

@Service
@Slf4j
public class PortScannerResultDiffServiceImpl implements PortScannerResultDiffService {
    private final Javers javers;

    @Value
    private static class ChangesHolder {
        private final SetMultimap<Class<? extends BaseEntity>, PropertyChange> changesMultimap = LinkedHashMultimap.create();

        public void addChange(final Class<? extends BaseEntity> _baseEntityClass,
                              final PropertyChange _propertyChange) {
            changesMultimap.put(_baseEntityClass, _propertyChange);
        }
    }

    public PortScannerResultDiffServiceImpl() {
        javers = JaversBuilder.javers().build();
    }

    @Override
    public PortScannerDiff diff(@NonNull final PortScannerResult _oldResult,
                                @NonNull final PortScannerResult _newResult) {
        final ChangesHolder changesHolder = new ChangesHolder();

        // find removed networks, new networks and networks with changes
        final Map<String, NetworkResult> oldResultsMap = _oldResult.getNetworkResultMapByNetworkId();
        final Map<String, NetworkResult> newResultsMap = _newResult.getNetworkResultMapByNetworkId();

        // set of new
        final Set<String> newNetworkResults = new HashSet<>(newResultsMap.keySet());
        newNetworkResults.removeAll(oldResultsMap.keySet());

        for(final String networkId : newNetworkResults) {
            addChange(changesHolder,
                    _newResult,
                    PropertyChange.addition(createGlobalId(_newResult, newResultsMap.get(networkId), null), Type.OBJECT, null, null, null));
        }

        // set of removed
        final Set<String> removedNetworkResults = new HashSet<>(oldResultsMap.keySet());
        removedNetworkResults.removeAll(newResultsMap.keySet());

        for(final String networkId : removedNetworkResults) {
            addChange(changesHolder,
                    _oldResult,
                    PropertyChange.removal(createGlobalId(_oldResult, oldResultsMap.get(networkId), null), Type.OBJECT, null, null, null));
        }

        // retained set
        final Set<String> retainedNetworkResults = new HashSet<>(oldResultsMap.keySet());
        retainedNetworkResults.retainAll(newResultsMap.keySet());

        // perform deep property comparison of a pair of NetworkResults using JaVers
        for(final String networkId : retainedNetworkResults) {
            final NetworkResult oldNetworkResult = oldResultsMap.get(networkId);
            final NetworkResult newNetworkResult = newResultsMap.get(networkId);
            final Diff diff = javers.compare(oldNetworkResult, newNetworkResult);

            diffNetwork(changesHolder, diff, _oldResult, _newResult, oldNetworkResult, newNetworkResult);
        }

        return PortScannerDiff.from(changesHolder.getChangesMultimap().asMap());
    }

    private void diffNetwork(@NonNull final ChangesHolder _changesHolder,
                             @NonNull final Diff _diff,
                             @NonNull final PortScannerResult _oldResult,
                             @NonNull final PortScannerResult _newResult,
                             @NonNull final NetworkResult _oldNetworkResult,
                             @NonNull final NetworkResult _newNetworkResult) {
        log.debug("Summary: " + _diff.changesSummary());

        for (final Change change : _diff.getChanges()) {
            if (!change.getAffectedObject().isPresent()) {
                throw new IllegalStateException("Affected object not present");
            }

            final Object affectedObject = change.getAffectedObject().get();
            final BaseEntity baseEntity = (change instanceof ObjectRemoved) ?
                    findBaseEntity(affectedObject, _oldResult.traceObjectPath(affectedObject)) :
                    findBaseEntity(affectedObject, _newResult.traceObjectPath(affectedObject));

            if (baseEntity == null || !(baseEntity instanceof PortScannerResult)) {
                throw new IllegalStateException("Base entity not a PortScannerResult - change: " + change);
            }

            final GlobalId globalId = createGlobalId(
                    (PortScannerResult) baseEntity,
                    (change instanceof ObjectRemoved) ? _oldNetworkResult : _newNetworkResult,
                    change);

            if (change instanceof NewObject) {
                addChange(_changesHolder, baseEntity, PropertyChange.addition(globalId, Type.OBJECT, null, null, null));
            } else if (change instanceof ObjectRemoved) {
                addChange(_changesHolder, baseEntity, PropertyChange.removal(globalId, Type.OBJECT, null, null, null));
            } else if (change instanceof ValueChange) {
                final ValueChange valueChange = (ValueChange) change;

                final PropertyChange propertyChangeDTO =
                        PropertyChange.valueModification(globalId, Type.OBJECT, valueChange.getPropertyName(), null, valueChange.getLeft().toString(), valueChange.getRight().toString());

                addChange(_changesHolder, baseEntity, propertyChangeDTO);
            } else if (change instanceof CollectionChange) {
                final CollectionChange collectionChange = (CollectionChange) change;

                for(final ContainerElementChange containerElementChange : collectionChange.getChanges()) {
                    handleContainerElementChange(_changesHolder, baseEntity, globalId, collectionChange, containerElementChange, Type.COLLECTION);
                }
            } else if (change instanceof ArrayChange) {
                final ArrayChange arrayChange = (ArrayChange) change;

                for(final ContainerElementChange containerElementChange : arrayChange.getChanges()) {
                    handleContainerElementChange(_changesHolder, baseEntity, globalId, arrayChange, containerElementChange, Type.ARRAY);
                }
            } else if (change instanceof MapChange) {
                final MapChange mapChange = (MapChange) change;

                for(final EntryChange entryChange : mapChange.getEntryChanges()) {
                    if (entryChange instanceof EntryAdded) {
                        final EntryAdded entryAdded = (EntryAdded) entryChange;

                        addChange(_changesHolder, baseEntity, PropertyChange.addition(globalId, Type.MAP, mapChange.getPropertyName(), entryAdded.getKey().toString(), entryAdded.getValue().toString()));
                    } else if (entryChange instanceof EntryRemoved) {
                        final EntryRemoved entryRemoved = (EntryRemoved) entryChange;

                        addChange(_changesHolder, baseEntity, PropertyChange.removal(globalId, Type.MAP, mapChange.getPropertyName(), entryRemoved.getKey().toString(), entryRemoved.getValue().toString()));
                    } else if (entryChange instanceof EntryValueChange) {
                        final EntryValueChange entryValueChange = (EntryValueChange) entryChange;

                        addChange(_changesHolder, baseEntity, PropertyChange.valueModification(globalId, Type.MAP, mapChange.getPropertyName(), entryValueChange.getKey().toString(), entryValueChange.getLeftValue().toString(), entryValueChange.getRightValue().toString()));
                    } else {
                        throw new IllegalArgumentException("Unhandled EntryChange class: " + entryChange.getClass().getCanonicalName());
                    }

                }
            } else {
                throw new IllegalArgumentException("Unhandled Change class: " + change.getClass().getCanonicalName());
            }
        }
    }

    private static GlobalId createGlobalId(@NonNull final PortScannerResult _portScannerResult,
                                           @NonNull final NetworkResult _networkResult,
                                           @Nullable final Change _change) {
        // create faked JaVers style global ID / selector
        final String selectorBase = PortScannerResult.class.getCanonicalName() + "/" +  _portScannerResult.getId() + "#networkResults/" + _portScannerResult.getNetworkResults().indexOf(_networkResult);

        if (_change != null) {
            final String affectedPath = _change.getAffectedGlobalId().toString();
            final int indexOfHash = affectedPath.indexOf('#');

            if (indexOfHash >= 0) {
                return new GlobalId(_portScannerResult.getId(), selectorBase.concat("/").concat(affectedPath.substring(indexOfHash + 1)));
            }
        }

        return new GlobalId(_portScannerResult.getId(), selectorBase);
    }

    private void handleContainerElementChange(@NonNull final ChangesHolder _changesHolder,
                                              @NonNull final BaseEntity _baseEntity,
                                              @NonNull final GlobalId _globalId,
                                              @NonNull final ContainerChange _containerChange,
                                              @NonNull final ContainerElementChange containerElementChange,
                                              @NonNull final Type _type) {
        if (containerElementChange instanceof ValueAdded) {
            final ValueAdded valueAdded = (ValueAdded) containerElementChange;

            addChange(_changesHolder, _baseEntity, PropertyChange.addition(_globalId, _type, _containerChange.getPropertyName(), valueAdded.getIndex().toString(), valueAdded.getAddedValue().toString()));
        } else if (containerElementChange instanceof ValueRemoved) {
            final ValueRemoved valueRemoved = (ValueRemoved) containerElementChange;

            addChange(_changesHolder, _baseEntity, PropertyChange.removal(_globalId, _type, _containerChange.getPropertyName(), valueRemoved.getIndex().toString(), valueRemoved.getRemovedValue().toString()));
        } else if (containerElementChange instanceof ElementValueChange) {
            final ElementValueChange elementValueChange = (ElementValueChange) containerElementChange;

            addChange(_changesHolder, _baseEntity, PropertyChange.valueModification(_globalId, _type, _containerChange.getPropertyName(), elementValueChange.getIndex().toString(), elementValueChange.getLeftValue().toString(), elementValueChange.getRightValue().toString()));
        } else {
            throw new IllegalArgumentException("Unhandled ContainerElementChange class: " + containerElementChange.getClass().getCanonicalName());
        }
    }

    private void addChange(@NonNull final ChangesHolder _changesHolder,
                           @NonNull final BaseEntity _baseEntity,
                           @NonNull final PropertyChange _propertyChange) {
        log.debug("Adding change: %s", _propertyChange.toString());

        _changesHolder.addChange(_baseEntity.getClass(), _propertyChange);
    }

    @Nullable
    private static BaseEntity findBaseEntity(@NonNull final Object _affectedObject,
                                             @Nullable final List<ObjectTreeAware> _path) {
        if (_affectedObject instanceof BaseEntity) {
            return (BaseEntity) _affectedObject;
        } else if (_path != null) {
            Optional<ObjectTreeAware> objectTreeAware = _path
                    .stream()
                    .filter(p -> p instanceof BaseEntity)
                    .findFirst();

            return objectTreeAware.isPresent() ? (BaseEntity) objectTreeAware.get() : null;
        }

        return null;
    }
}
