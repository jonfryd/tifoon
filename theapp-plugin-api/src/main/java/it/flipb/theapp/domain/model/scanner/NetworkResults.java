package it.flipb.theapp.domain.model.scanner;

import lombok.*;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class NetworkResults {
    @NonNull
    private List<NetworkResult> networkResults;

    public boolean hasResults() {
        return !this.getNetworkResults().isEmpty();
    }

    public int count() {
        return this.getNetworkResults().size();
    }
}
