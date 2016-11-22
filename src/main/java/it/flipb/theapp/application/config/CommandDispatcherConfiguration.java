package it.flipb.theapp.application.config;

import it.flipb.theapp.application.config.properties.application.Application;
import it.flipb.theapp.application.config.properties.application.CommandExecutor;
import it.flipb.theapp.infrastructure.service.dispatcher.CommandDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommandDispatcherConfiguration {
    @Autowired
    private Application application;

    @Autowired
    @Qualifier("dockerCommandDispatcherImpl")
    private CommandDispatcher dockerCommandDispatcher;

    @Bean
    public CommandDispatcher configuredCommandDispatcher()
    {
        final CommandExecutor commandExecutor = application.getCommandExecutor();

        if (commandExecutor.equals(CommandExecutor.docker)) {
          return dockerCommandDispatcher;
        }

        throw new RuntimeException(String.format("dispatcher '%s' not supported", commandExecutor));
    }
}
