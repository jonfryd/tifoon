package it.flipb.theapp.domain.model.masterplan;

import it.flipb.theapp.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.annotation.Nullable;

@Data
@NoArgsConstructor
public class Scanner implements Validator {
    private boolean active;
    @Nullable
    private String toolName;

    @Override
    public void validate() {
        Assert.hasLength(toolName, "toolName must have length");
    }
}
