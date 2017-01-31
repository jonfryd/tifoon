package it.flipb.theapp.domain.model.scanner;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@Embeddable
@SuppressFBWarnings("EI_EXPOSE_REP")
public class JonTester {
    private int[] sjover;

    @ElementCollection
    private Map<String, String> fisk = new HashMap<>();

    public JonTester(final int[] _sjover) {
        sjover = _sjover;

        fisk.put("g", "h");
        fisk.put("g5", "hf");
    }
}
