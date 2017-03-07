package com.elixlogic.tifoon.domain.model.object;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nullable;
import javax.persistence.*;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
public abstract class BaseEntity extends ReflectionObjectTreeAware {
}
