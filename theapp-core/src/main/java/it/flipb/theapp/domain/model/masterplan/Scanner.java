package it.flipb.theapp.domain.model.masterplan;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Scanner {
    private boolean active;
    @NonNull
    private String toolName;
}
