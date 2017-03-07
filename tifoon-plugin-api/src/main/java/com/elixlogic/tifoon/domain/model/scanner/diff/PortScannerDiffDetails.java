package com.elixlogic.tifoon.domain.model.scanner.diff;

import com.elixlogic.tifoon.domain.model.scanner.Protocol;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PortScannerDiffDetails implements Serializable {
    private String oldPortScannerResultId;
    private Long oldPortScanBeganAt;

    private String newPortScannerResultId;
    private Long newPortScanBeganAt;

    private List<String> newNetworkIds; // [networkId]
    private List<String> removedNetworkIds;
    private List<String> changedNetworkIds;

    private Map<String, List<String>> newOpenHostsMap; // [networkId]->[hosts]
    private Map<String, List<String>> removedOpenHostsMap;
    private Map<String, List<String>> changedOpenHostsMap;

    private Map<String, Map<String, Map<Protocol, List<Integer>>>> newOpenPortsTree; // [networkId]->[host]->[protocol]->[port numbers]
    private Map<String, Map<String, Map<Protocol, List<Integer>>>> removedOpenPortsTree;
}
