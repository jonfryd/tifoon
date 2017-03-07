package com.elixlogic.tifoon.plugin.io;

import lombok.NonNull;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.metadata.MetadataProvider;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface IoPlugin extends Plugin<String>, MetadataProvider {
    @Nullable
    <T> T load(InputStream _inputStream,
               Class<T> _rootClass,
               List<ListProperty> _listProperties,
               List<MapProperty> _mapProperties);

    void save(OutputStream _outputStream,
              Object _object,
              List<Class<?>> _asStringClasses);

    String getDefaultFileExtension();

    List<String> getFileExtensionsHandled();
}
