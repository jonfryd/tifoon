package com.elixlogic.tifoon.domain.model.scanner;

import com.elixlogic.tifoon.domain.model.object.BaseEntity;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.annotations.CascadeType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.*;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED) // must be protected for JPA
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class PortScannerResult extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Nullable
    private String id;

    private long beganAt;
    private long endedAt;
    private PortScannerStatus status;

    @NonNull
    @Embedded
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @Cascade(CascadeType.ALL)
    private List<NetworkResult> networkResults = Collections.unmodifiableList(new ArrayList<>());

    public void setNetworkResults(@Nullable final List<NetworkResult> _networkResults) {
        networkResults = _networkResults != null ? _networkResults : Collections.unmodifiableList(new ArrayList<>());
    }

    @Nonnull
    public static PortScannerStatus calculateStatus(@NonNull final List<NetworkResult> _networkResults) {
        if (_networkResults.isEmpty()) {
            return PortScannerStatus.DONE;
        } else {
            final long successCount = _networkResults
                    .stream()
                    .map(NetworkResult::isSuccess)
                    .filter(Predicate.isEqual(true))
                    .count();

            if (successCount == 0) {
                return PortScannerStatus.FAILURE;
            } else if (successCount < _networkResults.size()) {
                // partially done
                return PortScannerStatus.INCOMPLETE;
            } else {
                return PortScannerStatus.DONE;
            }
        }
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