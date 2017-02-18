package it.flipb.theapp.domain.model.scanner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.InetAddress;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PortScannerJob {
    private String networkId;
    private List<InetAddress> addresses;
    private List<PortRange> portRanges;
}
