package com.elixlogic.tifoon.domain.model.network;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IanaServiceEntries {
    private List<IanaServiceEntry> ianaServiceEntries;
}
