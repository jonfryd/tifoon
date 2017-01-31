package it.flipb.theapp.infrastructure.jpa.repositories;

import it.flipb.theapp.domain.model.scanner.PortScannerResult;
import org.springframework.data.repository.CrudRepository;

public interface PortScannerResultRepository extends CrudRepository<PortScannerResult, Long> {
}
