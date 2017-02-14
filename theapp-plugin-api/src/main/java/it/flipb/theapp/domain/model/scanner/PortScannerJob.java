package it.flipb.theapp.domain.model.scanner;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.util.List;

@Data
@NoArgsConstructor
public class PortScannerJob {
    @NonNull
    private String networkId;
    @NonNull
    private List<InetAddress> addresses;
    @NonNull
    private List<PortRange> portRanges;

    @SuppressFBWarnings("NP_NONNULL_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
    public PortScannerJob(final String _networkId,
                          final List<InetAddress> _addresses,
                          final List<PortRange> _portRanges) {
        setNetworkId(_networkId);
        setAddresses(_addresses);
        setPortRanges(_portRanges);
    }

    private void setNetworkId(final String _networkId) {
        Assert.hasLength(_networkId, "networkId must have length");
        networkId = _networkId;
    }

    private void setAddresses(final List<InetAddress> _addresses) {
        Assert.notEmpty(_addresses, "addresses cannot be empty");
        addresses = _addresses;
    }

    private void setPortRanges(final List<PortRange> _portRanges) {
        Assert.notEmpty(_portRanges, "port ranges cannot be empty");
        portRanges = _portRanges;
    }
}
