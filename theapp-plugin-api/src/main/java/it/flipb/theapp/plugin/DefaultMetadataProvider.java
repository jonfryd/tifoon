package it.flipb.theapp.plugin;

import org.springframework.plugin.metadata.MetadataProvider;
import org.springframework.plugin.metadata.PluginMetadata;
import org.springframework.plugin.metadata.SimplePluginMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DefaultMetadataProvider implements MetadataProvider {
    private static final String PROPERTY_NAME = "version";
    private static final String UNDEFINED = "UNDEFINED";
    private static final String META_INF_THEAPP = "/META-INF/theapp/";

    private final String pluginDescriptorName;

    public DefaultMetadataProvider(final String pluginDescriptorName) {
        this.pluginDescriptorName = pluginDescriptorName;
    }

    @Override
    public PluginMetadata getMetadata() {
        final String name = pluginDescriptorName;
        final String version = readVersion();

        return new SimplePluginMetadata(name, version);
    }

    protected String readVersion() {
        try (final InputStream is = getClass().getResourceAsStream(META_INF_THEAPP + getPluginDescriptorName())) {
            final Properties properties = new Properties();
            properties.load(is);

            final Object version = properties.get(PROPERTY_NAME);
            if (version != null) {
                return version.toString();
            }
        }
        catch (IOException _e) {
        }

        return UNDEFINED;
    }

    protected String getPluginDescriptorName() {
        return pluginDescriptorName;
    }
}
