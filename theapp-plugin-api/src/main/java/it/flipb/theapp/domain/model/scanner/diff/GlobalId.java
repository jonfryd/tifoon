package it.flipb.theapp.domain.model.scanner.diff;

import lombok.*;

import javax.annotation.Nullable;
import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GlobalId {
    private final static Comparator<GlobalId> BY_ENTITY_ID = Comparator.comparing(GlobalId::getEntityId);
    private final static Comparator<GlobalId> BY_SELECTOR = Comparator.comparing(GlobalId::getSelector);

    public final static Comparator<GlobalId> ORDERING = BY_ENTITY_ID.thenComparing(BY_SELECTOR);

    @Nullable
    private Long entityId;
    @Nullable
    private String selector;
}
