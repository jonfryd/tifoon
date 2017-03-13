package com.elixlogic.tifoon.domain.service.scanner.impl;

import com.elixlogic.tifoon.domain.model.network.IanaServiceEntries;
import com.elixlogic.tifoon.domain.model.network.IanaServiceEntry;
import com.elixlogic.tifoon.domain.model.scanner.Port;
import com.elixlogic.tifoon.domain.model.scanner.Protocol;
import com.elixlogic.tifoon.domain.service.scanner.WellKnownPortsLookupService;
import com.elixlogic.tifoon.infrastructure.config.PluginConfiguration;
import com.elixlogic.tifoon.plugin.io.IoPlugin;
import com.google.common.base.Preconditions;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WellKnownPortsLookupServiceImpl implements WellKnownPortsLookupService {
    private static final String SERVICE_NAMES_PORT_NUMBERS_YML = "service-names-port-numbers.yml";

    private static final boolean loadFromUrlAndConvertToLocalFile = false; // set to true when occasionally creating an updated version, otherwise keep at false

    private final IoPlugin yamlIoPlugin;

    private final Map<Port, List<IanaServiceEntry>> portIanaServiceMap;

    @Autowired
    public WellKnownPortsLookupServiceImpl(final PluginConfiguration _pluginConfiguration) {
        yamlIoPlugin = Preconditions.checkNotNull(_pluginConfiguration.getIoPluginByExtension("yml"), "YAML plugin not found");

        portIanaServiceMap = initMapping(loadIanaServiceEntries());
    }

    private Map<Port, List<IanaServiceEntry>> initMapping(final IanaServiceEntries _ianaServiceEntries) {
        return _ianaServiceEntries.getIanaServiceEntries()
                .stream()
                .collect(Collectors.groupingBy(
                        p -> Port.from(Protocol.valueOf(p.getTransportProtocol().toUpperCase()), Integer.valueOf(p.getPortNumber()))));
    }

    private IanaServiceEntries loadIanaServiceEntries() {
        try {
            if (loadFromUrlAndConvertToLocalFile) {
                log.info("Trying to load and convert IANA ports from the web...");

                // parsing the CSV file is rather slow, which is why we convert to YAML
                final HeaderColumnNameMappingStrategy<IanaServiceEntry> strategy = new HeaderColumnNameMappingStrategy<>();
                strategy.setType(IanaServiceEntry.class);

                final CsvToBean<IanaServiceEntry> csvToBean = new CsvToBean<>();
                final URLConnection conn = new URL("https://www.iana.org/assignments/service-names-port-numbers/service-names-port-numbers.csv").openConnection();
                final List<IanaServiceEntry> ianaServiceEntries = csvToBean.parse(strategy, new CSVReader(new InputStreamReader(conn.getInputStream())));
                final Set<String> knownProtocols = EnumSet.allOf(Protocol.class).stream().map(Enum::name).collect(Collectors.toSet());

                final IanaServiceEntries filtered = new IanaServiceEntries(ianaServiceEntries.stream()
                        .filter(s -> s.getPortNumber() != null &&
                                !s.getPortNumber().contains("-") &&
                                s.getTransportProtocol() != null &&
                                knownProtocols.contains(s.getTransportProtocol().toUpperCase())
                        )
                        .collect(Collectors.toList()));

                final File file = new File(SERVICE_NAMES_PORT_NUMBERS_YML);

                yamlIoPlugin.save(new FileOutputStream(file), filtered, Collections.emptyList());
                log.info("IANA ports were loaded and converted successfully.");

                return filtered;
            } else {
                final InputStream inputStream = new ClassPathResource(SERVICE_NAMES_PORT_NUMBERS_YML).getInputStream();

                return yamlIoPlugin.load(inputStream, IanaServiceEntries.class, Collections.emptyList(), Collections.emptyList());
            }
        } catch (Exception _e) {
            log.error("IANA ports reader could not be initialized", _e);

            return new IanaServiceEntries();
        }
    }

    @Override
    public Optional<List<IanaServiceEntry>> getServiceByName(final Port _port) {
        return Optional.ofNullable(portIanaServiceMap.get(_port));
    }
}
