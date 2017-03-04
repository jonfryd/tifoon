package com.elixlogic.tifoon.domain.model.masterplan;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Data
@NoArgsConstructor
public class Scanner implements Validator {
    private boolean active;
    private String toolName;

    @Override
    public void validate() {
        if (active) {
            Assert.hasLength(toolName, "toolName must have length");
        }
    }
}
