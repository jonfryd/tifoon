package com.elixlogic.tifoon.domain.service.reporting.impl;

import com.elixlogic.tifoon.domain.model.core.AppSettings;
import com.elixlogic.tifoon.domain.model.core.CoreSettings;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;
import com.elixlogic.tifoon.domain.service.reporting.ReportGeneratorService;
import com.elixlogic.tifoon.domain.service.reporting.WellKnownPortsLookupService;
import com.elixlogic.tifoon.domain.util.TimeHelper;
import com.lowagie.text.pdf.BaseFont;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Service
@Slf4j
public class ReportGeneratorServiceImpl implements ReportGeneratorService {
    private final WellKnownPortsLookupService wellKnownPortsLookupService;
    private final TemplateEngine templateEngine;

    @Autowired
    public ReportGeneratorServiceImpl(final WellKnownPortsLookupService _wellKnownPortsLookupService,
                                      final TemplateEngine _templateEngine) {
        wellKnownPortsLookupService = _wellKnownPortsLookupService;
        templateEngine = _templateEngine;
    }

    @Override
    public String generateHtml(final boolean _includeHeaderAndFooter,
                               @NonNull final CoreSettings _coreSettings,
                               @NonNull final AppSettings _appSettings,
                               @NonNull final List<PortScannerJob> _portScannerJobs,
                               @NonNull final PortScannerResult _portScannerResult,
                               @NonNull final PortScannerDiff _portScannerDiff,
                               @NonNull final PortScannerDiffDetails _portScannerDiffDetails) {
        // Prepare the evaluation context
        final Context ctx = new Context(Locale.forLanguageTag("en"));

        final Calendar scanBeganAt = Calendar.getInstance();
        scanBeganAt.setTime(TimeHelper.toDate(_portScannerResult.getBeganAt()));

        final Calendar scanEndedAt = Calendar.getInstance();
        scanEndedAt.setTime(TimeHelper.toDate(_portScannerResult.getEndedAt()));

        final String version = Optional.ofNullable(getClass().getPackage().getImplementationVersion()).orElse("0.x.y");

        final Map<String, String> criticalApplicationSettings = new LinkedHashMap<>();
        criticalApplicationSettings.put("config.tifoon.scanner.active", String.valueOf(_coreSettings.getScanner().isActive()));
        criticalApplicationSettings.put("config.tifoon.scanner.toolName", _coreSettings.getScanner().getToolName());
        criticalApplicationSettings.put("config.tifoon.scanner.additionalParameters", _coreSettings.getScanner().getAdditionalParameters());
        criticalApplicationSettings.put("config.tifoon.commandExecutor", _coreSettings.getCommandExecutor());
        criticalApplicationSettings.put("config.tifoon.scanRateSeconds", String.valueOf(_appSettings.getScanRateSeconds()));
        criticalApplicationSettings.put("config.tifoon.onlySaveReportOnChange", String.valueOf(_appSettings.isOnlySaveReportOnChange()));
        criticalApplicationSettings.put("config.tifoon.dynamicBaselineMode", String.valueOf(_appSettings.isDynamicBaselineMode()));
        criticalApplicationSettings.put("config.tifoon.useInitialScanAsBaseline", String.valueOf(_appSettings.isUseInitialScanAsBaseline()));
        criticalApplicationSettings.put("config.tifoon.baselineFilename", _appSettings.getBaselineFilename());

        ctx.setVariable("includeHeaderAndFooter", _includeHeaderAndFooter);
        ctx.setVariable("applicationVersion", version);
        ctx.setVariable("startedBy", System.getProperty("user.name"));
        ctx.setVariable("executedFrom", getLocalMachineHostname());
        ctx.setVariable("scanStartTime", scanBeganAt);
        ctx.setVariable("scanEndTime", scanEndedAt);
        ctx.setVariable("scanStatus", _portScannerResult.getStatus());
        ctx.setVariable("changesDetected", !_portScannerDiff.isUnchanged());
        ctx.setVariable("applicationSettings", criticalApplicationSettings);
        ctx.setVariable("portScannerJobs", _portScannerJobs);
        ctx.setVariable("portScannerDiffDetails", _portScannerDiffDetails);
        ctx.setVariable("portScannerResult", _portScannerResult);
        ctx.setVariable("wellKnownPortsLookupService", wellKnownPortsLookupService);

        return templateEngine.process("scanReport", ctx);
    }

    private static String getLocalMachineHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException _e) {
            log.warn("Unable to retrieve local machine hostname", _e);

            return "";
        }
    }

    @Override
    @Nullable
    public byte[] generatePdf(final String _html) {
        try {
            final ITextRenderer renderer = new ITextRenderer();
            final ITextFontResolver fontResolver = renderer.getFontResolver();

            final ClassPathResource regular = new ClassPathResource("fonts/LiberationSerif-Regular.ttf");
            fontResolver.addFont(regular.getURL().toString(), BaseFont.IDENTITY_H, true);

            renderer.setDocumentFromString(_html);
            renderer.layout();

            @Cleanup final ByteArrayOutputStream os = new ByteArrayOutputStream();
            renderer.createPDF(os);

            return os.toByteArray();
        } catch(Exception _e) {
            log.error("Failed to generate PDF", _e);
            return null;
        }
    }
}
