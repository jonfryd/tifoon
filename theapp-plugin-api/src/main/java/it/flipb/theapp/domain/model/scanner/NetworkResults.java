package it.flipb.theapp.domain.model.scanner;

import it.flipb.theapp.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Embeddable
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class NetworkResults extends ReflectionObjectTreeAware {
    @NonNull
    @Embedded
    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private List<NetworkResult> networkResults;

    public boolean hasResults() {
        return !this.getNetworkResults().isEmpty();
    }

    public int count() {
        return this.getNetworkResults().size();
    }
}
