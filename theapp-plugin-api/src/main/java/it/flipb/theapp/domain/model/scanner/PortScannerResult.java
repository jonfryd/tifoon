package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PortScannerResult extends BaseEntity {
    @NonNull
    private Long beganAt;
    @NonNull
    private Long endedAt;
    @NonNull
    private Boolean success;

    @NonNull
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "portScannerResultId", referencedColumnName = "id")
    private List<NetworkResult> networkResults;

    public long calcExecutionTimeMillis() {
        return getEndedAt() - getBeganAt();
    }
}
