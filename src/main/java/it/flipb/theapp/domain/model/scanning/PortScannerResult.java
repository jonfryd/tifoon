package it.flipb.theapp.domain.model.scanning;

import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

/*
 * Immutable
 */
public class PortScannerResult {
    private final String description;
    private final Map<InetAddress, List<Port>> openPortsMap;

    public PortScannerResult(final String _description, final Map<InetAddress, List<Port>> _openPortsMap) {
        description = _description;
        openPortsMap = _openPortsMap;
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    @NotNull
    public Map<InetAddress, List<Port>> getOpenPortsMap() {
        return openPortsMap;
    }

    @Override
    public String toString() {
        return "PortScannerResult{" +
                "description='" + description + '\'' +
                ", openPortsMap=" + openPortsMap +
                '}';
    }
}
