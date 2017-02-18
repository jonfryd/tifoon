package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OpenHost extends ReflectionObjectTreeAware implements Serializable {
    private String host;
    private List<OpenPort> openPorts;

    public static OpenHost from(@NonNull final String _hostAddress, @NonNull final List<Port> _ports) {
        return new OpenHost(_hostAddress, _ports
                .stream()
                .map(OpenPort::new)
                .collect(Collectors.toList()));
    }
}
