package com.elixlogic.tifoon.domain.service.reporting.impl;

import com.elixlogic.tifoon.domain.model.core.AppSettings;
import com.elixlogic.tifoon.domain.model.core.CoreSettings;
import com.elixlogic.tifoon.domain.model.core.Mail;
import com.elixlogic.tifoon.domain.model.core.Reporting;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;
import com.elixlogic.tifoon.domain.service.reporting.ReportEmailSenderService;
import com.elixlogic.tifoon.domain.service.reporting.ReportFileIOService;
import com.elixlogic.tifoon.domain.service.reporting.ReportGeneratorService;
import com.elixlogic.tifoon.domain.service.reporting.ReportingService;
import com.elixlogic.tifoon.domain.util.TimeHelper;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ReportingServiceImpl implements ReportingService {
    private final ReportGeneratorService reportGeneratorService;
    private final ReportFileIOService reportFileIOService;
    private final ReportEmailSenderService reportEmailSenderService;

    @Autowired
    public ReportingServiceImpl(final ReportGeneratorService _reportGeneratorService,
                                final ReportFileIOService _reportFileIOService,
                                final ReportEmailSenderService _reportEmailSenderService) {
        reportGeneratorService = _reportGeneratorService;
        reportFileIOService = _reportFileIOService;
        reportEmailSenderService = _reportEmailSenderService;
    }

    @Override
    public void report(@NonNull final CoreSettings _coreSettings,
                       @NonNull final AppSettings _appSettings,
                       @NonNull final String _pathAndBaseFilename,
                       @NonNull final List<PortScannerJob> _portScannerJobs,
                       @NonNull final PortScannerResult _portScannerResult,
                       @NonNull final PortScannerDiff _portScannerDiff,
                       @NonNull final PortScannerDiffDetails _portScannerDiffDetails) {
        final Reporting reporting = _coreSettings.getReporting();

        // do nothing if reporting is off
        if (!reporting.doReporting())
        {
            log.warn("All HTML/PDF reporting is disabled");
            return;
        }

        final String html = reportGeneratorService.generateHtml(_coreSettings,
                _appSettings,
                _portScannerJobs,
                _portScannerResult,
                _portScannerDiff,
                _portScannerDiffDetails);
        final byte[] pdf = (reporting.isEmailPdf() || reporting.isSavePdf()) ? reportGeneratorService.generatePdf(html) : null;

        final String formattedBeganAt = TimeHelper.formatTimestamp(_portScannerResult.getBeganAt());

        if (reporting.isSaveHtml()) {
            final String filename = _pathAndBaseFilename.concat(formattedBeganAt).concat(".html");

            log.info("Saving HTML report: {}", filename);
            reportFileIOService.saveFileAsUTF8(filename, html);
        }
        if (reporting.isSavePdf() && pdf != null) {
            final String filename = _pathAndBaseFilename.concat(formattedBeganAt).concat(".pdf");

            log.info("Saving PDF report: {}", filename);
            reportFileIOService.saveFile(filename, pdf);
        }
        if (reporting.isEmailHtml() || reporting.isEmailPdf()) {
            final String body = reporting.isEmailHtml() ? html : "Please see attached Tifoon scan PDF report";
            final String filename = reporting.isEmailPdf() ? "tifoon_scan_".concat(formattedBeganAt).concat(".pdf") : null;

            log.info("Sending e-mail...");
            reportEmailSenderService.sendEmail(_coreSettings.getMail(), body, filename, pdf);
        }
    }
}
