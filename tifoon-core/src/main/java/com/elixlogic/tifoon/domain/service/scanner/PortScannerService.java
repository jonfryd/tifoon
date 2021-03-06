package com.elixlogic.tifoon.domain.service.scanner;

import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;

import javax.annotation.Nullable;
import java.util.List;

public interface PortScannerService {
    PortScannerResult scan(List<PortScannerJob> _request, @Nullable final String _additionalParameters);
}
