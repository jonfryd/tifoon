package com.elixlogic.tifoon.domain.service.reporting;

import com.elixlogic.tifoon.domain.model.core.AppSettings;
import com.elixlogic.tifoon.domain.model.core.CoreSettings;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;

import javax.annotation.Nullable;

public interface ReportGeneratorService {
    String generateHtml(boolean _includeHeaderAndFooter,
                        CoreSettings _coreSettings,
                        AppSettings _appSettings,
                        PortScannerResult _baselinePortScannerResult,
                        PortScannerResult _portScannerResult,
                        PortScannerDiff _portScannerDiff,
                        PortScannerDiffDetails _portScannerDiffDetails);

    @Nullable
    byte[] generatePdf(String _html);
}
