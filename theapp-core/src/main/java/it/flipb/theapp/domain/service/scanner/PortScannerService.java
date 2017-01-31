package it.flipb.theapp.domain.service.scanner;

import it.flipb.theapp.domain.model.scanner.PortScannerJob;
import it.flipb.theapp.domain.model.scanner.PortScannerResult;
import lombok.NonNull;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PortScannerService {
    @NonNull
    PortScannerResult scan(@NonNull List<PortScannerJob> _request);

    @Transactional
    @NonNull
    PortScannerResult scanAndPersist(@NonNull List<PortScannerJob> _request);
}
