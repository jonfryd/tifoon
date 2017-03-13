package com.elixlogic.tifoon.domain.service.scanner;

import com.elixlogic.tifoon.domain.model.network.IanaServiceEntry;
import com.elixlogic.tifoon.domain.model.scanner.Port;

import java.util.List;
import java.util.Optional;

public interface WellKnownPortsLookupService {
    Optional<List<IanaServiceEntry>> getServiceByName(Port _port);
}
