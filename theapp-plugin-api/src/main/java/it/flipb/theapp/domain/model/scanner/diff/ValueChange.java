package it.flipb.theapp.domain.model.scanner.diff;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class ValueChange {
    @NonNull
    private String property;
    @NonNull
    private String previousValue;
    @NonNull
    private String newValue;
}
