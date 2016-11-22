package it.flipb.theapp.infrastructure.config.properties.tools;

import it.flipb.theapp.domain.model.tools.DockerImage;
import it.flipb.theapp.domain.model.tools.ShellCommand;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties(locations = {"classpath:tools.yml", "classpath:config/tools.yml", "file:tools.yml", "file:config/tools.yml"}, prefix = "tools")
public class Tools {
    @NotNull
    @Valid
    private List<DockerImage> dockerImages;

    @NotNull
    @Valid
    private List<ShellCommand> shellCommands;

    @NotNull
    public List<DockerImage> getDockerImages() {
        return dockerImages;
    }

    public void setDockerImages(@NotNull final List<DockerImage> _dockerImages) {
        dockerImages = _dockerImages;
    }

    @NotNull
    public List<ShellCommand> getShellCommands() {
        return shellCommands;
    }


    public void setShellCommands(@NotNull final List<ShellCommand> _shellCommands) {
        shellCommands = _shellCommands;

    }
}