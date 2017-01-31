package it.flipb.theapp.domain.model.masterplan;

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
    @NotEmpty
    private String ioFormat;

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

    @NotNull
    public String getIoFormat() {
        return ioFormat;
    }

    public void setIoFormat(@NotNull final String _ioFormat) {
        ioFormat = _ioFormat;
    }

    @Override
    public String toString() {
        return "MasterPlan{" +
                "scanner=" + scanner +
                ", commandExecutor='" + commandExecutor + '\'' +
                ", ioFormat='" + ioFormat + '\'' +
                '}';
    }
}