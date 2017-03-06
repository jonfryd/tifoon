package com.elixlogic.tifoon.domain.service.scanner;

import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;

public interface PortScannerStatsService {
    PortScannerDiffDetails createDetails(PortScannerResult _oldPortScannerResult,
                                         PortScannerResult _newPortScannerResult,
                                         PortScannerDiff _portScannerDiff);
}
