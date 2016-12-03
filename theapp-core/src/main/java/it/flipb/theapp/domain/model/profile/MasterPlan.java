package it.flipb.theapp.domain.model.profile;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@ConfigurationProperties(locations = {"classpath:masterplan.yml", "classpath:config/masterplan.yml", "file:masterplan.yml", "file:config/masterplan.yml"}, prefix = "masterplan")
public class MasterPlan {
    @NotNull
    @Valid
    private Scanner scanner;

    @NotNull
    @NotEmpty
    private String commandExecutor;

    @NotNull
    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(@NotNull final Scanner _scanner) {
        scanner = _scanner;
    }

    @NotNull
    public String getCommandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(@NotNull final String _commandExecutor) {
        commandExecutor = _commandExecutor;
    }

    @Override
    public String toString() {
        return "MasterPlan{" +
                "scanner=" + scanner +
                ", commandExecutor='" + commandExecutor + '\'' +
                '}';
    }
}