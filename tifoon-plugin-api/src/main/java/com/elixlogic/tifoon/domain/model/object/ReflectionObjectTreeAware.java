package com.elixlogic.tifoon.domain.model.object;

import lombok.NonNull;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ReflectionObjectTreeAware implements ObjectTreeAware {
    @Override
    @Nullable
    public final List<ObjectTreeAware> traceObjectPath(@NonNull final Object _objectToFind) {
        for (final Field field : FieldUtils.getAllFieldsList(getClass())) {
            try {
                final List<ObjectTreeAware> result = findValue(FieldUtils.readField(this, field.getName(), true), _objectToFind);

                if (result != null) {
                    return result;
                }
           } catch (IllegalAccessException _e) {
                // safe to ignore
            }
        }

        return null;
    }

    @Nullable
    private List<ObjectTreeAware> findValue(@NonNull final Object _value, @NonNull final Object _objectToFind) {
        List<ObjectTreeAware> result = null;

        if (_value == _objectToFind) {
            result = new ArrayList<>();
            result.add(this);
        } else if (_value instanceof ObjectTreeAware) {
            // recurse
            result = ((ObjectTreeAware) _value).traceObjectPath(_objectToFind);

            if (result != null) {
                result.add(this);
            }
        } else if (_value instanceof Collection) {
            // iterate collection
            for (final Object objectFromCollection : (Collection) _value) {
                result = findValue(objectFromCollection, _objectToFind);

                if (result != null) {
                    break;
                }
            }
        }

        return result;
    }
}
