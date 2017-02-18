package it.flipb.theapp.domain.model.scanner;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressFBWarnings("EI_EXPOSE_REP")
public class JonTester implements Serializable {
    private int[] sjover;

    private Map<String, String> fisk = new HashMap<>();

    public JonTester(final int[] _sjover) {
        sjover = _sjover;

        fisk.put("g", "h");
        fisk.put("g5", "hf");
    }
}
