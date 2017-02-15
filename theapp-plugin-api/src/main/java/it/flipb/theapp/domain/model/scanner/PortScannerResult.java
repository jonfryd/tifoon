package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PortScannerResult extends BaseEntity {
    private long beganAt;
    private long endedAt;
    private boolean success;

    @NonNull
    @Embedded
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<NetworkResult> networkResults = Collections.unmodifiableList(new ArrayList<>());

    public void setNetworkResults(@Nullable final List<NetworkResult> _networkResults) {
        networkResults = _networkResults != null ? _networkResults : Collections.unmodifiableList(new ArrayList<>());
    }

    public long calcExecutionTimeMillis() {
        return getEndedAt() - getBeganAt();
    }

    public boolean hasResults() {
        return !this.getNetworkResults().isEmpty();
    }

    public int numberOfResults() {
        return this.getNetworkResults().size();
    }

    public Map<String, NetworkResult> getNetworkResultMapByNetworkId() {
        return networkResults
                .stream()
                .collect(Collectors.toMap(NetworkResult::getNetworkId, Function.identity()));
    }
}