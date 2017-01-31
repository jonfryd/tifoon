package it.flipb.theapp.domain.service.scanner;

import it.flipb.theapp.domain.model.scanner.PortScannerJob;
import it.flipb.theapp.domain.model.scanner.PortScannerResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PortScannerService {
    PortScannerResult scan(List<PortScannerJob> _request);

    @Transactional
    PortScannerResult scanAndPersist(List<PortScannerJob> _request);
}
