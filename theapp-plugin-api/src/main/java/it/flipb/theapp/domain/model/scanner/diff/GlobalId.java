package it.flipb.theapp.domain.model.scanner.diff;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class GlobalId {
    @NonNull
    Long entityId;
    @NonNull
    private String selector;
}
