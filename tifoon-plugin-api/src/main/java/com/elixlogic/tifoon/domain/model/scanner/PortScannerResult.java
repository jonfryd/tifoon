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
    private List<PortScannerJob> portScannerJobs = Collections.unmodifiableList(new ArrayList<>());

    private String jobsHash;

    @NonNull
    @Embedded
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @Cascade(CascadeType.ALL)
    private List<NetworkResult> networkResults = Collections.unmodifiableList(new ArrayList<>());

    public void setPortScannerJobs(@Nullable final List<PortScannerJob> _portScannerJobs) {
        portScannerJobs = _portScannerJobs != null ? _portScannerJobs : Collections.unmodifiableList(new ArrayList<>());
    }

    private void updateJobsHash() {
        setJobsHash(Integer.toHexString(portScannerJobs.hashCode()));
    }

    public void setNetworkResults(@Nullable final List<NetworkResult> _networkResults) {
        networkResults = _networkResults != null ? _networkResults : Collections.unmodifiableList(new ArrayList<>());
    }

    private void updateStatus() {
        if (networkResults.isEmpty()) {
            setStatus(PortScannerStatus.DONE);
        } else {
            final long successCount = networkResults
                    .stream()
                    .map(NetworkResult::isSuccess)
                    .filter(Predicate.isEqual(true))
                    .count();

            if (successCount == 0) {
                setStatus(PortScannerStatus.FAILURE);
            } else if (successCount < networkResults.size()) {
                // partially done
                setStatus(PortScannerStatus.INCOMPLETE);
            } else {
                setStatus(PortScannerStatus.DONE);
            }
        }
    }

    public void update() {
        updateJobsHash();
        updateStatus();
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