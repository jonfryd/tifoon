package it.flipb.theapp.application.config.properties.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@ConfigurationProperties(prefix = "application")
public class Application {
    @NotNull
    @Valid
    private CommandExecutor commandExecutor;

    @NotNull
    @Valid
    private Scanner scanner;

    @NotNull
    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }

    public void setCommandExecutor(@NotNull final CommandExecutor _commandExecutor) {
        commandExecutor = _commandExecutor;
    }

    @NotNull
    public Scanner getScanner() {
        return scanner;
    }

    public void setScanner(@NotNull final Scanner _scanner) {
        scanner = _scanner;
    }

    @Override
    public String toString() {
        return "Application{" +
                "commandExecutor=" + commandExecutor +
                ", scanner=" + scanner +
                '}';
    }
}