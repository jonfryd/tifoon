package it.flipb.theapp.domain.service.scanning;

import it.flipb.theapp.domain.model.scanning.PortScannerJob;
import it.flipb.theapp.domain.model.scanning.PortScannerResult;

public interface PortScannerService {
  PortScannerResult scan(PortScannerJob _request);
}
