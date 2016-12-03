package it.flipb.theapp.domain.model.network;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

import java.net.InetAddress;
import java.util.List;

public class Target {
    @NotNull
    @NotEmpty
    private String description;

    @NotNull
    @NotEmpty
    private List<InetAddress> addresses;

    @NotNull
    @NotEmpty
    private List<String> ports;

    public String getDescription() {
        return description;
    }

    public void setDescription(@NotNull final String _description) {
        this.description = _description;
    }

    public List<InetAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(@NotNull final List<InetAddress> _addresses) {
        this.addresses = _addresses;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(@NotNull final List<String> _ports) {
        this.ports = _ports;
    }

    @Override
    public String toString() {
        return "Target{" +
            "description=" + description +
            ", addresses=" + addresses +
            ", ports=" + ports +
            '}';
    }
}
