package it.flipb.theapp.config.properties;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

import java.net.InetAddress;
import java.util.List;

public class Target {
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private List<InetAddress> addresses;

    @NotNull
    @NotEmpty
    private List<String> ports;

    public String getName() {
        return name;
    }

    public void setName(final String _name) {
        this.name = _name;
    }

    public List<InetAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(final List<InetAddress> _addresses) {
        this.addresses = _addresses;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(final List<String> _ports) {
        this.ports = _ports;
    }

    @Override
    public String toString() {
        return "Target{" +
            "name=" + name +
            ", addresses=" + addresses +
            ", ports=" + ports +
            '}';
    }
}
