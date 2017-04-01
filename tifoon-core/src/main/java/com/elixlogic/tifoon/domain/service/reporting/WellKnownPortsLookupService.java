package com.elixlogic.tifoon.domain.service.reporting;

import com.elixlogic.tifoon.domain.model.network.IanaServiceEntry;
import com.elixlogic.tifoon.domain.model.scanner.Port;
import com.elixlogic.tifoon.domain.model.scanner.Protocol;

import java.util.List;
import java.util.Optional;

public interface WellKnownPortsLookupService {
    Optional<List<IanaServiceEntry>> getServices(Port _port);

    String getFormattedServiceNames(Protocol _protocol, int _portNumber);

    String getSingleFormattedServiceDescription(Protocol _protocol, int _portNumber);
}
