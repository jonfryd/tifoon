package it.flipb.theapp.domain.model.scanning;

import java.net.InetAddress;
import java.util.List;

/*
 * Immutable
 */
public class PortScannerResult {
    private InetAddress address;
    private List<Port> openPorts;

}
