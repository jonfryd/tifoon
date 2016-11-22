package it.flipb.theapp.infrastructure.repository.command.impl;

import it.flipb.theapp.domain.model.tools.DockerImage;
import it.flipb.theapp.domain.model.tools.ShellCommand;
import it.flipb.theapp.infrastructure.config.ToolsConfiguration;
import it.flipb.theapp.infrastructure.repository.command.CommandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CommandRepositoryImpl implements CommandRepository {
    @NotNull
    private Map<String, DockerImage> dockerImageMap;

    @NotNull
    private Map<String, ShellCommand> shellCommandMap;

    @Autowired
    public CommandRepositoryImpl(final ToolsConfiguration _toolsConfiguration) {
        Assert.notNull(_toolsConfiguration, "command configuration cannot be null");

        dockerImageMap = _toolsConfiguration.getTools().getDockerImages()
                .stream()
                .collect(Collectors.toMap(i -> i.getCommand(), i -> i));
        shellCommandMap = _toolsConfiguration.getTools().getShellCommands()
                .stream()
                .collect(Collectors.toMap(i -> i.getCommand(), i -> i));
    }

    @Override
    @Null
    public DockerImage findDockerImage(final String _command) {
        Assert.hasLength(_command, "command must have length");

        return dockerImageMap.get(_command);
    }

    @Override
    @Null
    public ShellCommand findShellCommand(final String _command) {
        Assert.hasLength(_command, "command must have length");

        return shellCommandMap.get(_command);
    }
}
