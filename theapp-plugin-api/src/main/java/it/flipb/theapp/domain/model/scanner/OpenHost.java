package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class OpenHost extends ReflectionObjectTreeAware {
    @NonNull
    private String host;
    @NonNull
    private List<OpenPort> openPorts;

    public static OpenHost from(final String _hostAddress, final List<Port> _ports) {
        return new OpenHost(_hostAddress, _ports
                .stream()
                .map(p -> new OpenPort(p))
                .collect(Collectors.toList()));
    }
}
