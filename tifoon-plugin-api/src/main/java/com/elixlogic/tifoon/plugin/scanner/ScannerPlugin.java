package com.elixlogic.tifoon.plugin.scanner;

import com.elixlogic.tifoon.domain.model.scanner.NetworkResult;
import com.elixlogic.tifoon.domain.model.scanner.PortScannerJob;
import com.elixlogic.tifoon.plugin.executer.ExecutorPlugin;
import org.springframework.plugin.core.Plugin;
import org.springframework.plugin.metadata.MetadataProvider;

import javax.annotation.Nullable;

public interface ScannerPlugin extends Plugin<String>, MetadataProvider {
    @Nullable
    NetworkResult scan(PortScannerJob _request, ExecutorPlugin _executorPlugin);
}
