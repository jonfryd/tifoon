package it.flipb.theapp.domain.model.docker;

import it.flipb.theapp.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.List;

@ConfigurationProperties(locations = {"classpath:docker.yml", "classpath:config/docker.yml", "file:docker.yml", "file:config/docker.yml"}, prefix = "docker")
@Data
@NoArgsConstructor
public class DockerConfiguration implements Validator {
    @Nullable
    private DockerImage defaultImage;
    @Nullable
    private List<DockerImage> customImages;

    @Nullable
    public DockerImage getDefaultImage() {
        return defaultImage;
    }

    public void setDefaultImage(@NonNull String defaultImage) {
        this.defaultImage = new DockerImage(defaultImage);
    }

    @Override
    public void validate() {
        Assert.notNull(defaultImage, "defaultImage cannot be null");
        defaultImage.validate();

        if (customImages != null) {
            customImages.forEach(DockerImage::validate);
        }
    }
}