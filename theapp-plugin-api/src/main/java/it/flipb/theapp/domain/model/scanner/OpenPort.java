package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class OpenPort extends ReflectionObjectTreeAware {
    @NonNull
    private Port port;
}
