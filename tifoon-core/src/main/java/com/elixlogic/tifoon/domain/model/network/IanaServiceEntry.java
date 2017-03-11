package com.elixlogic.tifoon.domain.model.network;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IanaServiceEntry {
    @CsvBindByName(column = "Service Name")
    private String serviceName;
    @CsvBindByName(column = "Port Number")
    private String portNumber; // Note: can actually be a range of ports!
    @CsvBindByName(column = "Transport Protocol")
    private String transportProtocol;
    @CsvBindByName(column = "Description")
    private String description;
}
