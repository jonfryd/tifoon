package com.elixlogic.tifoon.domain.service.scanner.impl;

import com.elixlogic.tifoon.domain.model.plugin.CorePlugin;
import com.elixlogic.tifoon.domain.model.scanner.*;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiff;
import com.elixlogic.tifoon.domain.model.scanner.diff.PortScannerDiffDetails;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerFileIOService;
import com.elixlogic.tifoon.domain.service.scanner.PortScannerLoggingService;
import com.elixlogic.tifoon.domain.util.TimeHelper;
import com.elixlogic.tifoon.infrastructure.config.PluginConfiguration;
import com.elixlogic.tifoon.plugin.io.IoPlugin;
import com.elixlogic.tifoon.plugin.io.MapProperty;
import com.google.common.io.Files;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@SuppressFBWarnings(value = "OBL_UNSATISFIED_OBLIGATION", justification = "https://github.com/findbugsproject/findbugs/issues/98")
public class PortScannerFileIOServiceImpl implements PortScannerFileIOService {
    private final PortScannerLoggingService portScannerLoggingService;
    private final PluginConfiguration pluginConfiguration;
    private final CorePlugin<IoPlugin> saveCorePlugin;

    @Autowired
    public PortScannerFileIOServiceImpl(final PortScannerLoggingService _portScannerLoggingService,
                                        final PluginConfiguration _pluginConfiguration,
                                        final CorePlugin<IoPlugin> _saveCorePlugin) {
        portScannerLoggingService = _portScannerLoggingService;
        pluginConfiguration = _pluginConfiguration;
        saveCorePlugin = _saveCorePlugin;
    }

    @Override
    public PortScannerResult loadPortScannerResult(@NonNull final String _filename,
                                                   @NonNull final PortScannerResult _defaultPortScannerResult) {
        final File file = new File(_filename);

        try {
            @Cleanup final FileInputStream fis = new FileInputStream(file);

            log.info("Loading file: " + _filename);

            final String extension = Files.getFileExtension(file.getPath());
            final IoPlugin ioPluginForExtension = pluginConfiguration.getIoPluginByExtension(extension);

            if (ioPluginForExtension != null) {
                // extra mapping meta-data required by YAML plugin, ignored by JSON plugin
                // (Jackson is much smarter with regard to inferring types it seems)
                final MapProperty openHostsMapProperty = new MapProperty(NetworkResult.class, "openHosts", String.class, OpenHost.class);
                final MapProperty openPortsMapProperty = new MapProperty(OpenHost.class, "openPorts", Integer.class, Port.class);
                final PortScannerResult portScannerResult = ioPluginForExtension.load(fis, PortScannerResult.class, Collections.emptyList(), Arrays.asList(openHostsMapProperty, openPortsMapProperty));

                if (portScannerResult != null) {
                    log.info("Port scan result loaded.");

                    return portScannerResult;
                } else {
                    log.warn("Unable to deserialize scan result.");
                }
            } else {
                log.warn("Unable to find registered I/O plugin for extension: " + extension);
            }
        } catch (IOException _e) {
            log.warn("Failed to load port scan result", _e);
        }

        return _defaultPortScannerResult;
    }

    @Override
    public void savePortScannerResults(@NonNull final String _pathAndBaseFilename,
                                       @NonNull final PortScannerResult _baselinePortScannerResult,
                                       @NonNull final PortScannerResult _newPortScannerResult,
                                       @NonNull final PortScannerDiff _portScannerDiff,
                                       @NonNull final PortScannerDiffDetails _portScannerDiffDetails) {
        final String formattedBeganAt = TimeHelper.formatTimestamp(_newPortScannerResult.getBeganAt());

        final File portScannerResultFile = new File(_pathAndBaseFilename.concat(formattedBeganAt) + "." + saveCorePlugin.getExtension().getDefaultFileExtension());

        saveObject(portScannerResultFile, _newPortScannerResult, Collections.emptyList());

        // only save diff when there are changes to report
        if (!_portScannerDiff.isUnchanged()) {
            portScannerLoggingService.logDiffDetails(_portScannerDiffDetails);

            final String baselineFormattedBeganAt = TimeHelper.formatTimestamp(_baselinePortScannerResult.getBeganAt());
            final File portScannerDiffFile = new File(_pathAndBaseFilename.concat(baselineFormattedBeganAt) + "_diff_" + formattedBeganAt + "." + saveCorePlugin.getExtension().getDefaultFileExtension());

            saveObject(portScannerDiffFile, _portScannerDiffDetails, Collections.singletonList(Protocol.class));
        }
    }

    private void saveObject(@NonNull final File _portScannerResultFile,
                            @NonNull final Object _objectToPersist,
                            @NonNull final List<Class<?>> _asStringClasses) {
        try {
            FileUtils.forceMkdirParent(_portScannerResultFile);
            final boolean success = _portScannerResultFile.createNewFile();

            if (!success) {
                log.debug("Output file already exists: {}", _portScannerResultFile.getPath());
            }

            @Cleanup final FileOutputStream fos = new FileOutputStream(_portScannerResultFile);

            log.info("Saving file: " + _portScannerResultFile.getPath());

            saveCorePlugin.getExtension().save(fos, _objectToPersist, _asStringClasses);
        } catch (IOException _e) {
            log.error("Failed to save port scan result", _e);
        }
    }
}
