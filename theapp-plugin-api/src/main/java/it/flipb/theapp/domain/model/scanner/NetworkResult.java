package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NetworkResult extends BaseEntity {
    @NonNull
    private String description;
    @NonNull
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "networkResultId", referencedColumnName = "id")
    private List<OpenHost> openHosts;

    public NetworkResult(final String _description, final Map<InetAddress, List<Port>> _openPortsMap) {
        description = _description;
        openHosts = new ArrayList<>(
                _openPortsMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().getHostAddress(), e -> OpenHost.from(e.getKey().getHostAddress(), e.getValue())))
                .values());
    }
}
