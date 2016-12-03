package it.flipb.theapp.domain.service.scanner;

import it.flipb.theapp.domain.model.scanner.PortScannerJob;
import it.flipb.theapp.domain.model.scanner.PortScannerResult;

public interface PortScannerService {
  PortScannerResult scan(PortScannerJob _request);
}
