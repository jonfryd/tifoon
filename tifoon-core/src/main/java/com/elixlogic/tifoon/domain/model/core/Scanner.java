package com.elixlogic.tifoon.domain.model.core;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.annotation.Nullable;

@Data
@NoArgsConstructor
public class Scanner implements Validator {
    private boolean active;
    private String toolName;
    @Nullable
    private String additionalParameters;

    @Override
    public void validate() {
        if (active) {
            Assert.hasLength(toolName, "toolName must have length");
        }
    }
}
