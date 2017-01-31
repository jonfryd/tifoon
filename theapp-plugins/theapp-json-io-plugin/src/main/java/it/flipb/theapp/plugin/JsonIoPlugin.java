package it.flipb.theapp.plugin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.flipb.theapp.plugin.io.AbstractIoPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class JsonIoPlugin extends AbstractIoPlugin {
    private static final String PROVIDES = "json";

    private static final Logger logger = LoggerFactory.getLogger(JsonIoPlugin.class);

    private final ObjectMapper mapper;

    public JsonIoPlugin() {
        mapper = new ObjectMapper();
    }

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    public <T> T load(final InputStream _inputStream, final Class<T> _rootClass) {
        Assert.notNull(_inputStream, "InputStream cannot be null");
        Assert.notNull(_rootClass, "Root class cannot be null");

        try {
            logger.debug("Loading json");

            return mapper.readValue(_inputStream, _rootClass);
        } catch (IOException _e) {
            logger.error("Error loading JSON", _e);

            return null;
        }
    }

    @Override
    public void save(final OutputStream _outputStream, final Object _object) {
        Assert.notNull(_outputStream, "OutputStream cannot be null");
        Assert.notNull(_object, "Object cannot be null");

        try {
            logger.debug("Saving json");

            mapper.writerWithDefaultPrettyPrinter().writeValue(_outputStream, _object);
        } catch (IOException _e) {
            logger.error("Error saving JSON", _e);
        }
    }
}
