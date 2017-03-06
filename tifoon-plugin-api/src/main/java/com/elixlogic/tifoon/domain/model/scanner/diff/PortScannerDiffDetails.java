package com.elixlogic.tifoon.domain.model.scanner.diff;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
public class PortScannerDiffDetails implements Serializable {
    private String oldPortScannerResultId;
    private String oldPortScanBeganAtTimestamp;

    private String newPortScannerResultId;
    private String newPortScanBeganAtTimestamp;

    private List<String> newNetworkIds; // key: [networkId]
    private List<String> removedNetworkIds;
    private List<String> changedNetworkIds;

    private List<String> newOpenHostKeys; // key: [networkId]/[host]
    private List<String> removedOpenHostKeys;
    private List<String> changedOpenHostKeys;

    private List<String> newOpenPortKeys; // key: [networkId]/[host]/[protocol]/[port number]
    private List<String> removedOpenPortKeys;
}
