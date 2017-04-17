package com.elixlogic.tifoon.domain.model.scanner;

import com.elixlogic.tifoon.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

@Embeddable
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PortScannerJob extends ReflectionObjectTreeAware implements Serializable {
    private String networkId;
    private ArrayList<Host> hosts = new ArrayList<>();
    private ArrayList<PortRange> portRanges = new ArrayList<>();
    private String jobHash;

    public PortScannerJob(@NonNull final String _networkId,
                          @NonNull final List<Host> _hosts,
                          @NonNull final List<PortRange> _portRanges) {
        networkId = _networkId;
        hosts = new ArrayList<>(_hosts);
        portRanges = new ArrayList<>(_portRanges);
        jobHash = Integer.toHexString(hashCode());
    }
}
