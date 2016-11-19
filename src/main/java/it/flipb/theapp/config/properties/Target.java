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

    public void setName(String name) {
        this.name = name;
    }

    public List<InetAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<InetAddress> addresses) {
        this.addresses = addresses;
    }

    public List<String> getPorts() {
        return ports;
    }

    public void setPorts(List<String> ports) {
        this.ports = ports;
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
