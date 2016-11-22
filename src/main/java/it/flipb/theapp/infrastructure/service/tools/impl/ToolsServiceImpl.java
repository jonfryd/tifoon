package it.flipb.theapp.infrastructure.service.tools.impl;

import it.flipb.theapp.domain.model.tools.DockerImage;
import it.flipb.theapp.domain.model.tools.ShellCommand;
import it.flipb.theapp.infrastructure.config.ToolsConfiguration;
import it.flipb.theapp.infrastructure.service.tools.ToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ToolsServiceImpl implements ToolsService {
    @NotNull
    private Map<String, DockerImage> dockerImageMap;

    @NotNull
    private Map<String, ShellCommand> shellCommandMap;

    @Autowired
    public ToolsServiceImpl(final ToolsConfiguration _toolsConfiguration) {
        Assert.notNull(_toolsConfiguration, "tools configuration cannot be null");

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
