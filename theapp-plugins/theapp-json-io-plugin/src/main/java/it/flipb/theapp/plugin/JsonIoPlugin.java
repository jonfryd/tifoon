package it.flipb.theapp.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.flipb.theapp.plugin.io.AbstractIoPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class JsonIoPlugin extends AbstractIoPlugin {
    private static final String PROVIDES = "json";

    private final ObjectMapper mapper;

    public JsonIoPlugin() {
        mapper = new ObjectMapper();
    }

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    public <T> T load(@NonNull final InputStream _inputStream,
                      @NonNull final Class<T> _rootClass) {
        try {
            log.debug("Loading json");

            return mapper.readValue(_inputStream, _rootClass);
        } catch (IOException _e) {
            log.error("Error loading JSON", _e);

            return null;
        }
    }

    @Override
    public void save(@NonNull final OutputStream _outputStream,
                     @NonNull final Object _object) {
        try {
            log.debug("Saving json");

            mapper.writerWithDefaultPrettyPrinter().writeValue(_outputStream, _object);
        } catch (IOException _e) {
            log.error("Error saving JSON", _e);
        }
    }
}
