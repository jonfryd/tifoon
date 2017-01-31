package it.flipb.theapp.domain.model.docker;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(locations = {"classpath:docker.yml", "classpath:config/docker.yml", "file:docker.yml", "file:config/docker.yml"}, prefix = "docker")
@Data
@NoArgsConstructor
public class DockerConfiguration {
    @NonNull
    private DockerImage defaultImage;
    @NonNull
    private List<DockerImage> customImages;

    @NonNull
    public DockerImage getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(@NonNull String defaultImage) {
        this.defaultImage = new DockerImage(defaultImage);
    }
}