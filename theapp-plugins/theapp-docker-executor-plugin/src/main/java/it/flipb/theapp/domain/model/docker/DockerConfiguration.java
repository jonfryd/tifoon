package it.flipb.theapp.domain.model.docker;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@ConfigurationProperties(locations = {"classpath:docker.yml", "classpath:config/docker.yml", "file:docker.yml", "file:config/docker.yml"}, prefix = "docker")
public class DockerConfiguration {
    @NotNull
    @Valid
    private DockerImage defaultImage;

    @NotNull
    @Valid
    private List<DockerImage> customImages;

    @NotNull
    public List<DockerImage> getCustomImages() {
        return customImages;
    }

    public void setCustomImages(@NotNull final List<DockerImage> _Custom_dockerImages) {
        customImages = _Custom_dockerImages;
    }

    @NotNull
    public DockerImage getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(@NotNull String defaultImage) {
        this.defaultImage = new DockerImage(defaultImage);
    }
}