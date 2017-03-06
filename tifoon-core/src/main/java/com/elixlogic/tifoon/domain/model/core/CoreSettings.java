package com.elixlogic.tifoon.domain.model.core;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

@ConfigurationProperties(prefix = "tifoon")
@Data
@NoArgsConstructor
public class CoreSettings implements Validator {
    private Scanner scanner;
    private String commandExecutor;
    private String saveFormat;

    @Override
    public void validate() {
        Assert.notNull(scanner, "scanner cannot be null");
        Assert.hasLength(commandExecutor, "commandExecutor must have length");
        Assert.hasLength(saveFormat, "saveFormat must have length");

        scanner.validate();
    }
}