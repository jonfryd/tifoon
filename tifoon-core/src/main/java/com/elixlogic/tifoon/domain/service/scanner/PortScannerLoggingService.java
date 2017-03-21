package com.elixlogic.tifoon.domain.service.scanner;

import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;

public interface PortScannerLoggingService {
    void logDiffDetails(PortScannerDiffDetails _portScannerDiffDetails);
}
