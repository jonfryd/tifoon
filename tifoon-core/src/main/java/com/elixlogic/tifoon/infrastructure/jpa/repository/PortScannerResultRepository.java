package com.elixlogic.tifoon.infrastructure.jpa.repository;

import com.elixlogic.tifoon.domain.model.scanner.PortScannerResult;
import org.springframework.data.repository.CrudRepository;

public interface PortScannerResultRepository extends CrudRepository<PortScannerResult, Long> {
}
