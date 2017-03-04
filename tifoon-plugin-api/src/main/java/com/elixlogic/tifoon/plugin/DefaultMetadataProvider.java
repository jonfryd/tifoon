package com.elixlogic.tifoon.plugin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.plugin.metadata.MetadataProvider;
import org.springframework.plugin.metadata.PluginMetadata;
import org.springframework.plugin.metadata.SimplePluginMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
@RequiredArgsConstructor
public class DefaultMetadataProvider implements MetadataProvider {
    private static final String PROPERTY_NAME = "version";
    private static final String UNDEFINED = "UNDEFINED";
    private static final String META_INF_TIFOON = "/META-INF/tifoon/";

    @Getter
    private final String pluginDescriptorName;

    @Override
    public PluginMetadata getMetadata() {
        final String version = readVersion();

        return new SimplePluginMetadata(pluginDescriptorName, version);
    }

    private String readVersion() {
        try (final InputStream is = getClass().getResourceAsStream(META_INF_TIFOON + getPluginDescriptorName())) {
            final Properties properties = new Properties();
            properties.load(is);

            final Object version = properties.get(PROPERTY_NAME);
            if (version != null) {
                return version.toString();
            }
        }
        catch (IOException _e) {
            log.warn("Cannot read plugin version for '%s': %s", getPluginDescriptorName(), _e.getMessage());
        }

        return UNDEFINED;
    }
}
