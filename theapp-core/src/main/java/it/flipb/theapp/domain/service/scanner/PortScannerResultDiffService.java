package it.flipb.theapp.domain.service.scanner;

import it.flipb.theapp.domain.model.scanner.diff.PortScannerDiff;
import it.flipb.theapp.domain.model.scanner.PortScannerResult;

public interface PortScannerResultDiffService {
    PortScannerDiff diff(PortScannerResult _oldResult, PortScannerResult _newResult);
}
