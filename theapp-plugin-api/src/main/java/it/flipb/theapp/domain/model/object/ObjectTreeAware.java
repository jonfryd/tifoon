package it.flipb.theapp.domain.model.object;

import java.util.List;

public interface ObjectTreeAware {
    List<ObjectTreeAware> traceObjectPath(Object _objectToFind);
}
