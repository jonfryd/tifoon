package it.flipb.theapp.domain.model.scanning;

import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.net.InetAddress;
import java.util.List;

public class PortScannerJob {
    private String description;
    private List<InetAddress> addresses;
    private List<PortRange> portRanges;

    private PortScannerJob() {
        // required for model mapping
    }

    public PortScannerJob(@NotNull final String _description,
                          @NotNull final List<InetAddress> _addresses,
                          @NotNull final List<PortRange> _portRanges) {
        setDescription(_description);
        setAddresses(_addresses);
        setPortRanges(_portRanges);
    }

    @NotNull
    public String getDescription() {
        return description;
    }

    private void setDescription(final String _description) {
        Assert.hasLength(_description, "description must have length");
        description = _description;
    }

    @NotNull
    public List<InetAddress> getAddresses() {
        return addresses;
    }

    private void setAddresses(final List<InetAddress> _addresses) {
        Assert.notEmpty(_addresses, "addresses cannot be empty");
        addresses = _addresses;
    }

    @NotNull
    public List<PortRange> getPortRanges() {
        return portRanges;
    }

    private void setPortRanges(final List<PortRange> _portRanges) {
        Assert.notEmpty(_portRanges, "port ranges cannot be empty");
        portRanges = _portRanges;
    }

    @Override
    public String toString() {
        return "PortScannerJob{" +
                "description='" + description + '\'' +
                ", addresses=" + addresses +
                ", portRanges=" + portRanges +
                '}';
    }
}
