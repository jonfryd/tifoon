package com.elixlogic.tifoon.domain.service.reporting;

import com.elixlogic.tifoon.domain.model.core.AppSettings;
import com.elixlogic.tifoon.domain.model.core.CoreSettings;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;

import java.util.List;

public interface ReportingService {
    void report(CoreSettings _coreSettings,
                AppSettings _appSettings,
                String _pathAndBaseFilename,
                List<PortScannerJob> _portScannerJobs,
                PortScannerResult _portScannerResult,
                PortScannerDiff _portScannerDiff,
                PortScannerDiffDetails _portScannerDiffDetails);
}
