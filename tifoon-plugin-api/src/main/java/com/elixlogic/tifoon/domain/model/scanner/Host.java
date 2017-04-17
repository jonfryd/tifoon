package com.elixlogic.tifoon.domain.model.scanner;

import com.elixlogic.tifoon.domain.model.object.ReflectionObjectTreeAware;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class Host extends ReflectionObjectTreeAware implements Serializable {
    private String hostName;
    private String hostAddress;
}
