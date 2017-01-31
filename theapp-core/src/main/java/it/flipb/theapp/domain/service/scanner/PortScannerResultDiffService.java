package it.flipb.theapp.domain.service.scanner;

import it.flipb.theapp.domain.model.scanner.diff.PortScannerDiff;
import it.flipb.theapp.domain.model.scanner.PortScannerResult;
import lombok.NonNull;

public interface PortScannerResultDiffService {
    @NonNull
    PortScannerDiff diff(@NonNull PortScannerResult _oldResult, @NonNull PortScannerResult _newResult);
}
