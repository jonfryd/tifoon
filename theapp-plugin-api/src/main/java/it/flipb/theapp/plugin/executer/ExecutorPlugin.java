package it.flipb.theapp.plugin.executer;

import lombok.NonNull;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.metadata.MetadataProvider;

public interface ExecutorPlugin extends Plugin<String>, MetadataProvider {
    byte[] dispatch(@NonNull String _command, @NonNull String[] _arguments, @NonNull String _outputFile);
}
