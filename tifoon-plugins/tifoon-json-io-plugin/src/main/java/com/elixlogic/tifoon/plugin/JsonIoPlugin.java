package com.elixlogic.tifoon.plugin;

import com.elixlogic.tifoon.plugin.io.AbstractIoPlugin;
import com.elixlogic.tifoon.plugin.io.ListProperty;
import com.elixlogic.tifoon.plugin.io.MapProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class JsonIoPlugin extends AbstractIoPlugin {
    private static final String PROVIDES = "json";
    private static final List<String> EXTENSIONS_HANDLED = ImmutableList.of(PROVIDES);

    private final ObjectMapper mapper;

    public JsonIoPlugin() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    @Nullable
    public <T> T load(@NonNull final InputStream _inputStream,
                      @NonNull final Class<T> _rootClass,
                      @NonNull final List<ListProperty> _listProperties,
                      @NonNull final List<MapProperty> _mapProperties) {
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
                     @NonNull final Object _object,
                     @NonNull final List<Class<?>> _asStringClasses) {
        try {
            log.debug("Saving json");

            mapper.writerWithDefaultPrettyPrinter().writeValue(_outputStream, _object);
        } catch (IOException _e) {
            log.error("Error saving JSON", _e);
        }
    }

    @Override
    public String getDefaultFileExtension() {
        return EXTENSIONS_HANDLED.get(0);
    }

    @Override
    public List<String> getFileExtensionsHandled() {
        return EXTENSIONS_HANDLED;
    }
}
