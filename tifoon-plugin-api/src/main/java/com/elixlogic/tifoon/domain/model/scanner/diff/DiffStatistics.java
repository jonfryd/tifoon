package com.elixlogic.tifoon.domain.model.scanner.diff;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiffStatistics {
    private String oldPortScannerResultId;
    private Long oldPortScanBeganAt;

    private String newPortScannerResultId;
    private Long newPortScanBeganAt;

    private int newNetworkResults;
    private int removedNetworkResults;
    private int changedNetworkResults;

    private int newOpenHosts;
    private int removedOpenHosts;
    private int changedOpenHosts;

    private int newOpenPorts;
    private int removedOpenPorts;
    private int changedOpenPorts;
}
