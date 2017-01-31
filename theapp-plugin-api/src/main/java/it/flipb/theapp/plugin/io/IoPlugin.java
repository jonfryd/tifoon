package it.flipb.theapp.plugin.io;

import lombok.NonNull;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.metadata.MetadataProvider;

import java.io.InputStream;
import java.io.OutputStream;

public interface IoPlugin extends Plugin<String>, MetadataProvider {
    @NonNull
    <T> T load(@NonNull InputStream _inputStream, @NonNull Class<T> _rootClass);

    void save(@NonNull OutputStream _outputStream, @NonNull Object _object);
}
