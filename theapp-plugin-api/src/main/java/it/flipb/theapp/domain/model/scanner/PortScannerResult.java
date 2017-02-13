package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
    @NonNull
    private Long beganAt;
    @NonNull
    private Long endedAt;
    @NonNull
    private Boolean success;

    @Embedded
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<NetworkResult> networkResults;

    public void setNetworkResults(final List<NetworkResult> _networkResults) {
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

    public Map<String, NetworkResult> networkResultMapByNetworkId() {
        return networkResults
                .stream()
                .collect(Collectors.toMap(NetworkResult::getNetworkId, Function.identity()));
    }
}