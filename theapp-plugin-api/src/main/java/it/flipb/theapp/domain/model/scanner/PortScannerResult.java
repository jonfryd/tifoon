package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.BaseEntity;
import it.flipb.theapp.domain.model.object.ReflectionObjectTreeAware;
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
    @Embedded
    private NetworkResults result;

    public long calcExecutionTimeMillis() {
        return getEndedAt() - getBeganAt();
    }
}