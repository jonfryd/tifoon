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
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.ListCompareAlgorithm;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.*;
import org.javers.core.diff.changetype.map.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class PortScannerResultDiffServiceImpl implements PortScannerResultDiffService {
    private final Javers javers;

    public PortScannerResultDiffServiceImpl() {
        javers = JaversBuilder.javers().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE).build();
    }

    @Override
    @NonNull
    public PortScannerDiff diff(@NonNull final PortScannerResult _oldResult, @NonNull final PortScannerResult _newResult) {
        // find diff
        final Diff diff = javers.compare(_oldResult.getResult(), _newResult.getResult());
        //final Diff diff = javers.compareCollections(_oldResult.getResult().getNetworkResults(), _newResult.getResult().getNetworkResults(), NetworkResult.class);

        log.debug(diff.prettyPrint());
        log.debug("Summary: " + diff.changesSummary());

        final SetMultimap<Class<? extends BaseEntity>, PropertyChange> entityChangeMultimap = LinkedHashMultimap.create();

        for (final Change change : diff.getChanges()) {
            if (!change.getAffectedObject().isPresent()) {
                // TODO: throw exception?!
                log.error("affected object not present - skipping!");
                continue;
            }

            final Object affectedObject = change.getAffectedObject().get();
            final BaseEntity baseEntity = (change instanceof ObjectRemoved) ?
                    findBaseEntity(affectedObject, _oldResult.traceObjectPath(affectedObject)) :
                    findBaseEntity(affectedObject, _newResult.traceObjectPath(affectedObject));

            if (baseEntity == null) {
                // TODO: throw exception!
                log.error("null base entity - skipping!");
                continue;
            }

            final GlobalId globalId = new GlobalId(baseEntity.getId(), change.getAffectedGlobalId().toString());

            if (change instanceof NewObject) {
                addChange(entityChangeMultimap, baseEntity, PropertyChange.addition(globalId, Type.OBJECT, null, null, null));
            } else if (change instanceof ObjectRemoved) {
                addChange(entityChangeMultimap, baseEntity, PropertyChange.removal(globalId, Type.OBJECT, null, null, null));
            } else if (change instanceof ValueChange) {
                final ValueChange valueChange = (ValueChange) change;

                final PropertyChange propertyChangeDTO =
                        PropertyChange.valueModification(globalId, Type.OBJECT, valueChange.getPropertyName(), null, valueChange.getLeft().toString(), valueChange.getRight().toString());

                addChange(entityChangeMultimap, baseEntity, propertyChangeDTO);
            } else if (change instanceof CollectionChange) {
                final CollectionChange collectionChange = (CollectionChange) change;

                for(final ContainerElementChange containerElementChange : collectionChange.getChanges()) {
                    handleContainerElementChange(entityChangeMultimap, baseEntity, globalId, collectionChange, containerElementChange, Type.COLLECTION);
                }
            } else if (change instanceof ArrayChange) {
                final ArrayChange arrayChange = (ArrayChange) change;

                for(final ContainerElementChange containerElementChange : arrayChange.getChanges()) {
                    handleContainerElementChange(entityChangeMultimap, baseEntity, globalId, arrayChange, containerElementChange, Type.ARRAY);
                }
            } else if (change instanceof MapChange) {
                final MapChange mapChange = (MapChange) change;

                for(final EntryChange entryChange : mapChange.getEntryChanges()) {
                    if (entryChange instanceof EntryAdded) {
                        final EntryAdded entryAdded = (EntryAdded) entryChange;

                        addChange(entityChangeMultimap, baseEntity, PropertyChange.addition(globalId, Type.MAP, mapChange.getPropertyName(), entryAdded.getKey().toString(), entryAdded.getValue().toString()));
                    } else if (entryChange instanceof EntryRemoved) {
                        final EntryRemoved entryRemoved = (EntryRemoved) entryChange;

                        addChange(entityChangeMultimap, baseEntity, PropertyChange.removal(globalId, Type.MAP, mapChange.getPropertyName(), entryRemoved.getKey().toString(), entryRemoved.getValue().toString()));
                    } else if (entryChange instanceof EntryValueChange) {
                        final EntryValueChange entryValueChange = (EntryValueChange) entryChange;

                        addChange(entityChangeMultimap, baseEntity, PropertyChange.valueModification(globalId, Type.MAP, mapChange.getPropertyName(), entryValueChange.getKey().toString(), entryValueChange.getLeftValue().toString(), entryValueChange.getRightValue().toString()));
                    } else {
                        // TODO: throw exception
                        log.error("UNHANDLED entry change type!");
                    }

                }

            } else {
                // TODO: throw exception
                log.error("UNHANDLED change type!");
            }
        }

        return PortScannerDiff.from(entityChangeMultimap.asMap());
    }

    private void handleContainerElementChange(final SetMultimap<Class<? extends BaseEntity>, PropertyChange> _entityChangeMultimap,
                                              final BaseEntity _baseEntity,
                                              final GlobalId _globalId,
                                              final ContainerChange _containerChange,
                                              final ContainerElementChange containerElementChange,
                                              final Type _type) {
        if (containerElementChange instanceof ValueAdded) {
            final ValueAdded valueAdded = (ValueAdded) containerElementChange;

            addChange(_entityChangeMultimap, _baseEntity, PropertyChange.addition(_globalId, _type, _containerChange.getPropertyName(), valueAdded.getIndex().toString(), valueAdded.getAddedValue().toString()));
        } else if (containerElementChange instanceof ValueRemoved) {
            final ValueRemoved valueRemoved = (ValueRemoved) containerElementChange;

            addChange(_entityChangeMultimap, _baseEntity, PropertyChange.removal(_globalId, _type, _containerChange.getPropertyName(), valueRemoved.getIndex().toString(), valueRemoved.getRemovedValue().toString()));
        } else if (containerElementChange instanceof ElementValueChange) {
            final ElementValueChange elementValueChange = (ElementValueChange) containerElementChange;

            addChange(_entityChangeMultimap, _baseEntity, PropertyChange.valueModification(_globalId, _type, _containerChange.getPropertyName(), elementValueChange.getIndex().toString(), elementValueChange.getLeftValue().toString(), elementValueChange.getRightValue().toString()));
        } else {
            // TODO: throw exception
            log.error("UNHANDLED container element change type!");
        }
    }

    private void addChange(final SetMultimap<Class<? extends BaseEntity>, PropertyChange> _entityChangeMultimap,
                           final BaseEntity _baseEntity,
                           final PropertyChange _propertyChange) {
        log.debug("Adding change: %s", _propertyChange.toString());

        _entityChangeMultimap.put(_baseEntity.getClass(), _propertyChange);
    }

    private static BaseEntity findBaseEntity(@NonNull final Object _affectedObject, final List<ObjectTreeAware> _path) {
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
