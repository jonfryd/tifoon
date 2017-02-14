package it.flipb.theapp.plugin.scanner;

import it.flipb.theapp.domain.model.scanner.PortScannerJob;
import it.flipb.theapp.domain.model.scanner.NetworkResult;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.metadata.MetadataProvider;

import javax.annotation.Nullable;

public interface ScannerPlugin extends Plugin<String>, MetadataProvider {
    @Nullable
    NetworkResult scan(PortScannerJob _request, ExecutorPlugin _executorPlugin);
}
