package it.flipb.theapp.plugin.executer;

import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.metadata.MetadataProvider;

public interface ExecutorPlugin extends Plugin<String>, MetadataProvider {
    byte[] dispatch(String _command, String[] _arguments, String _outputFile);
}
