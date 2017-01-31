package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OpenHost extends BaseEntity {
    @NonNull
    private String host;
    @NonNull
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "openHostId", referencedColumnName = "id")
    private List<OpenPort> openPorts;

    public static OpenHost from(final String _hostAddress, final List<Port> _ports) {
        return new OpenHost(_hostAddress, _ports
                .stream()
                .map(p -> new OpenPort(p))
                .collect(Collectors.toList()));
    }
}
