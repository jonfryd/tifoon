package com.elixlogic.tifoon.domain.service.scanner;

import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;

public interface PortScannerResultDiffService {
    PortScannerDiff diff(PortScannerResult _oldResult, PortScannerResult _newResult);
}
