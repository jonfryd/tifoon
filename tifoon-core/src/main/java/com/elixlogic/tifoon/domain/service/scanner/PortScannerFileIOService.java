package com.elixlogic.tifoon.domain.service.scanner;

import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;

public interface PortScannerFileIOService {
    PortScannerResult loadPortScannerResult(String _filename,
                                            PortScannerResult _defaultPortScannerResult);

    void savePortScannerResults(String _pathAndBaseFilename,
                                PortScannerResult _baselinePortScannerResult,
                                PortScannerResult _newPortScannerResult,
                                PortScannerDiff _portScannerDiff,
                                PortScannerDiffDetails _portScannerDiffDetails);
}
