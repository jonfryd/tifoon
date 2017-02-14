package it.flipb.theapp.domain.model.object;

import lombok.Data;

import javax.annotation.Nullable;
import javax.persistence.*;

@Data
@MappedSuperclass
public class BaseEntity extends ReflectionObjectTreeAware {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Nullable
    private Long id;
}
