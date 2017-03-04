package com.elixlogic.tifoon.domain.model.object;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.annotation.Nullable;
import javax.persistence.*;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = false)
public class BaseEntity extends ReflectionObjectTreeAware {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Nullable
    private String id;
}
