package com.elixlogic.tifoon.domain.model.scanner.diff;

import lombok.*;

import java.io.Serializable;
import java.util.Comparator;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class GlobalId implements Serializable {
    private final static Comparator<GlobalId> BY_ENTITY_ID = Comparator.comparing(GlobalId::getEntityId);
    private final static Comparator<GlobalId> BY_SELECTOR = Comparator.comparing(GlobalId::getSelector);

    public final static Comparator<GlobalId> ORDERING = BY_ENTITY_ID.thenComparing(BY_SELECTOR);

    private String entityId;
    private String selector;
}
