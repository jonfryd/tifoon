package it.flipb.theapp.domain.model.masterplan;

import it.flipb.theapp.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import javax.annotation.Nullable;

@ConfigurationProperties(locations = {"classpath:masterplan.yml", "classpath:config/masterplan.yml", "file:masterplan.yml", "file:config/masterplan.yml"}, prefix = "masterplan")
@Data
@NoArgsConstructor
public class MasterPlan implements Validator {
    private Scanner scanner;
    private String commandExecutor;
    private String ioFormat;

    @Override
    public void validate() {
        Assert.notNull(scanner, "scanner cannot be null");
        Assert.hasLength(commandExecutor, "commandExecutor must have length");
        Assert.hasLength(ioFormat, "ioFormat must have length");

        scanner.validate();
    }
}