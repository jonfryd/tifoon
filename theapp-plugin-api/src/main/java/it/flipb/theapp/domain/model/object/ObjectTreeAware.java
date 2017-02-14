package it.flipb.theapp.domain.model.object;

import javax.annotation.Nullable;
import java.util.List;

public interface ObjectTreeAware {
    @Nullable
    List<ObjectTreeAware> traceObjectPath(Object _objectToFind);
}
