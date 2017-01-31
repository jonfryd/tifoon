package it.flipb.theapp.domain.model.object;

import lombok.Data;

import javax.persistence.*;

@Data
@MappedSuperclass
public class BaseEntity extends ReflectionObjectTreeAware {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
}
