package it.flipb.theapp.domain.model.scanner;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import java.net.InetAddress;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class PortScannerJob {
    @Nullable
    private String networkId;
    @Nullable
    private List<InetAddress> addresses;
    @Nullable
    private List<PortRange> portRanges;
}
