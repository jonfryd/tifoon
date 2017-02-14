package it.flipb.theapp.plugin.executer;

import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.metadata.MetadataProvider;

import javax.annotation.Nullable;

public interface ExecutorPlugin extends Plugin<String>, MetadataProvider {
    @Nullable
    byte[] dispatch(String _command, String[] _arguments, String _outputFile);
}
