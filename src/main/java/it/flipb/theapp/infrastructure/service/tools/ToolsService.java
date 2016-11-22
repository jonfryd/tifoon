package it.flipb.theapp.infrastructure.service.tools;

import it.flipb.theapp.domain.model.tools.DockerImage;
import it.flipb.theapp.domain.model.tools.ShellCommand;

public interface ToolsService {
    DockerImage findDockerImage(String _command);
    ShellCommand findShellCommand(String _command);
}
