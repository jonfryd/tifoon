package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.BaseEntity;
import it.flipb.theapp.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;
import java.util.Optional;

@Entity
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NetworkResults extends BaseEntity {
    @NonNull
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    @JoinColumn(name = "networkResultsId", referencedColumnName = "id")
    private List<NetworkResult> networkResults;

    public boolean hasResults() {
        return !this.getNetworkResults().isEmpty();
    }

    public int count() {
        return this.getNetworkResults().size();
    }
}
