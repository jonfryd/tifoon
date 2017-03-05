package com.elixlogic.tifoon.domain.model.scanner.diff;

import com.elixlogic.tifoon.domain.model.scanner.Port;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DiffDetails {
    private String oldPortScannerResultId;
    private Long oldPortScanBeganAt;

    private String newPortScannerResultId;
    private Long newPortScanBeganAt;

    private List<String> newNetworkResultIds;
    private List<String> removedNetworkResultIds;
    private List<String> changedNetworkResultIds;

    private List<String> newOpenHostIds;
    private List<String> removedOpenHostIds;
    private List<String> changedOpenHostIds;

    private List<Port> newOpenPorts;
    private List<Port> removedOpenPorts;
    private List<Port> changedOpenPorts;
}
