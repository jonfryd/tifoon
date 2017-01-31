package it.flipb.theapp.domain.model.scanner;

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
    private String description;
    @NonNull
    private List<InetAddress> addresses;
    @NonNull
    private List<PortRange> portRanges;

    public PortScannerJob(final String _description,
                          final List<InetAddress> _addresses,
                          final List<PortRange> _portRanges) {
        setDescription(_description);
        setAddresses(_addresses);
        setPortRanges(_portRanges);
    }

    private void setDescription(final String _description) {
        Assert.hasLength(_description, "description must have length");
        description = _description;
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
