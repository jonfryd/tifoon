package it.flipb.theapp.domain.service.scanner.impl;

import com.google.common.collect.*;
import it.flipb.theapp.domain.model.object.BaseEntity;
import it.flipb.theapp.domain.model.object.ObjectTreeAware;
import it.flipb.theapp.domain.model.scanner.*;
import it.flipb.theapp.domain.model.scanner.diff.GenericChange;
import it.flipb.theapp.domain.model.scanner.diff.GlobalId;
import it.flipb.theapp.domain.model.scanner.diff.PortScannerDiff;
import it.flipb.theapp.domain.service.scanner.PortScannerResultDiffService;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PortScannerResultDiffServiceImpl implements PortScannerResultDiffService {
    private static final Logger logger = LoggerFactory.getLogger(PortScannerResultDiffServiceImpl.class);

    private final Javers javers;

    public PortScannerResultDiffServiceImpl() {
        javers = JaversBuilder.javers().build();
    }

    @Override
    public PortScannerDiff diff(final PortScannerResult _oldResult, final PortScannerResult _newResult) {
        // find diff
        final Diff diff = javers.compare(new NetworkResults(_oldResult.getNetworkResults()), new NetworkResults(_newResult.getNetworkResults()));

        //final Diff diff = javers.compareCollections(_oldResult.getNetworkResults(), _newResult.getNetworkResults(), NetworkResult.class);

        logger.debug(diff.prettyPrint());
        logger.debug("Summary: " + diff.changesSummary());

        final SetMultimap<String, GlobalId> objectsRemovedMultimap = LinkedHashMultimap.create();

        for(final ObjectRemoved objectRemoved : diff.getChangesByType(ObjectRemoved.class)) {
            System.out.println(objectRemoved);
            final Object affectedObject = objectRemoved.getAffectedObject().get();

            if (affectedObject instanceof BaseEntity) {
                final BaseEntity baseEntity = (BaseEntity) affectedObject;

                objectsRemovedMultimap.put(affectedObject.getClass().getCanonicalName(), new GlobalId(baseEntity.getId(), objectRemoved.getAffectedGlobalId().toString()));
            } else {
                final List<ObjectTreeAware> path = _newResult.traceObjectPath(affectedObject);
                final BaseEntity baseEntity = (BaseEntity) path
                        .stream()
                        .filter(p -> p instanceof BaseEntity)
                        .findFirst()
                        .get();
                objectsRemovedMultimap.put(baseEntity.getClass().getCanonicalName(), new GlobalId(baseEntity.getId(), objectRemoved.getAffectedGlobalId().toString()));
            }
        }

        final SetMultimap<String, GlobalId> objectsAddedMultimap = LinkedHashMultimap.create();

        for(final NewObject newObject : diff.getChangesByType(NewObject.class)) {
            System.out.println(newObject);
            final Object affectedObject = newObject.getAffectedObject().get();

            if (affectedObject instanceof BaseEntity) {
                final BaseEntity baseEntity = (BaseEntity) affectedObject;

                objectsAddedMultimap.put(affectedObject.getClass().getCanonicalName(), new GlobalId(baseEntity.getId(), newObject.getAffectedGlobalId().toString()));
            } else {
                final List<ObjectTreeAware> path = _newResult.traceObjectPath(affectedObject);
                final BaseEntity baseEntity = (BaseEntity) path
                        .stream()
                        .filter(p -> p instanceof BaseEntity)
                        .findFirst()
                        .get();
                objectsAddedMultimap.put(baseEntity.getClass().getCanonicalName(), new GlobalId(baseEntity.getId(), newObject.getAffectedGlobalId().toString()));
            }
        }

        //
        // ADDED:
        //   "PortScannerResult": [id1, id2, ...]
        //   "Port": [id1, id2, ...]
        //
        // CHANGED:
        //   "Port": [{id1, {property1, old1, new1}}, id2, ...]
        //
        //
        //

        final SetMultimap<String, GenericChange> objectsChangedMultimap = LinkedHashMultimap.create();

        for(final ValueChange valueChange : diff.getChangesByType(ValueChange.class)) {
            System.out.println(valueChange);
            System.out.println(valueChange.getAffectedGlobalId().getTypeName());

            final it.flipb.theapp.domain.model.scanner.diff.ValueChange valueChangeDTO =
                    new it.flipb.theapp.domain.model.scanner.diff.ValueChange(valueChange.getPropertyName(), valueChange.getLeft().toString(), valueChange.getRight().toString());
            final Object affectedObject = valueChange.getAffectedObject().get();

            final List<ObjectTreeAware> path = _newResult.traceObjectPath(affectedObject);

            if (path != null) {
                for(final ObjectTreeAware objectTreeAware : path) {
                    if (objectTreeAware instanceof BaseEntity) {
                        System.out.println(((BaseEntity) objectTreeAware).getId() + " " + objectTreeAware.getClass().getCanonicalName());
                    }
                }
            }

            if (affectedObject instanceof BaseEntity) {
                final BaseEntity baseEntity = (BaseEntity) affectedObject;

                objectsChangedMultimap.put(affectedObject.getClass().getCanonicalName(), new GenericChange(new GlobalId(baseEntity.getId(), valueChange.getAffectedGlobalId().toString()), valueChangeDTO));
            } else {
                final BaseEntity baseEntity = (BaseEntity) path
                        .stream()
                        .filter(p -> p instanceof BaseEntity)
                        .findFirst()
                        .get();

                objectsChangedMultimap.put(baseEntity.getClass().getCanonicalName(), new GenericChange(new GlobalId(baseEntity.getId(), valueChange.getAffectedGlobalId().toString()), valueChangeDTO));
            }
        }

        for(final ContainerChange containerChange : diff.getChangesByType(ContainerChange.class)) {
            System.out.println(containerChange.toString());
            System.out.println("NOT HANDLED!");
        }

        for(final MapChange mapChange : diff.getChangesByType(MapChange.class)) {
            System.out.println(mapChange.toString());
            System.out.println("NOT HANDLED!");
        }

        return PortScannerDiff.from(objectsRemovedMultimap.asMap(), objectsAddedMultimap.asMap(), objectsChangedMultimap.asMap());
    }
}
