package com.elixlogic.tifoon.domain.model.network;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.net.InetAddress;
import java.util.List;

@Data
@NoArgsConstructor
public class Target implements Validator {
    private String networkId;
    private List<InetAddress> addresses;
    private List<String> ports;

    @Override
    public void validate() {
        Assert.hasLength(networkId, "networkId must have length");
        Assert.notEmpty(addresses, "addresses must not be empty");
        Assert.notEmpty(ports, "ports must not be empty");
    }
}
