package it.flipb.theapp.domain.model.scanner.diff;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Comparator;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class GlobalId {
    private final static Comparator<GlobalId> BY_ENTITY_ID = Comparator.comparing(GlobalId::getEntityId);
    private final static Comparator<GlobalId> BY_SELECTOR = Comparator.comparing(GlobalId::getSelector);

    public final static Comparator<GlobalId> ORDERING = BY_ENTITY_ID.thenComparing(BY_SELECTOR);

    @NonNull
    Long entityId;
    @NonNull
    private String selector;
}
