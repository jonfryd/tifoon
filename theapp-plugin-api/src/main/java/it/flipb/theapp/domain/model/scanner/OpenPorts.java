package it.flipb.theapp.domain.model.scanner;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class OpenPorts {
    @NonNull
    private List<OpenPort> openPorts;

    public int count() {
        return this.getOpenPorts().size();
    }
}
