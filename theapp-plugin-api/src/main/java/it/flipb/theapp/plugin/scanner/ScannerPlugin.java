package it.flipb.theapp.plugin.scanner;

import it.flipb.theapp.domain.model.scanner.PortScannerJob;
import it.flipb.theapp.domain.model.scanner.PortScannerResult;
import it.flipb.theapp.plugin.executer.ExecutorPlugin;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.metadata.MetadataProvider;

public interface ScannerPlugin extends Plugin<String>, MetadataProvider {
    PortScannerResult scan(PortScannerJob _request, ExecutorPlugin _executorPlugin);
}
