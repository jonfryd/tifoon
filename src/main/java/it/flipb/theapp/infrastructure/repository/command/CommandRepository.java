package it.flipb.theapp.infrastructure.repository.command;

import it.flipb.theapp.domain.model.tools.DockerImage;
import it.flipb.theapp.domain.model.tools.ShellCommand;

public interface CommandRepository {
    DockerImage findDockerImage(String _command);
    ShellCommand findShellCommand(String _command);
}
