package it.flipb.theapp.domain.model.network;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.net.InetAddress;
import java.util.List;

@Data
@NoArgsConstructor
public class Target {
    @NonNull
    private String networkId;
    @NonNull
    private List<InetAddress> addresses;
    @NonNull
    private List<String> ports;
}
