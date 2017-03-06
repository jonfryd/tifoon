package com.elixlogic.tifoon.domain.model.scanner;

import com.elixlogic.tifoon.domain.model.object.BaseEntity;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // must be protected for JPA
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PortScannerResult extends BaseEntity implements Serializable {
    private long beganAt;
    private long endedAt;
    private boolean success;

    @NonNull
    @Embedded
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @Cascade(CascadeType.ALL)
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

    public Map<String, NetworkResult> mapNetworkResultsByNetworkId() {
        return networkResults
                .stream()
                .collect(Collectors.toMap(NetworkResult::getNetworkId, Function.identity()));
    }
}