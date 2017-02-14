package it.flipb.theapp.domain.model.network;

import it.flipb.theapp.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.util.List;

@Data
@NoArgsConstructor
public class Target implements Validator {
    @Nullable
    private String networkId;
    @Nullable
    private List<InetAddress> addresses;
    @Nullable
    private List<String> ports;

    @Override
    public void validate() {
        Assert.hasLength(networkId, "networkId must have length");
        Assert.notEmpty(addresses, "addresses must not be empty");
        Assert.notEmpty(ports, "ports must not be empty");
    }
}
