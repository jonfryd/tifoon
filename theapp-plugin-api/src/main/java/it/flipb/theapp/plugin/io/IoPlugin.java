package it.flipb.theapp.plugin.io;

import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.metadata.MetadataProvider;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.OutputStream;

public interface IoPlugin extends Plugin<String>, MetadataProvider {
    @Nullable
    <T> T load(InputStream _inputStream, Class<T> _rootClass);

    void save(OutputStream _outputStream, Object _object);
}
