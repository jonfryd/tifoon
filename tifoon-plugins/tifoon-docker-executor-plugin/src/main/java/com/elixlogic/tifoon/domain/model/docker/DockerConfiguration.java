package com.elixlogic.tifoon.domain.model.docker;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.List;

@ConfigurationProperties(prefix = "docker")
@Data
@NoArgsConstructor
public class DockerConfiguration implements Validator {
    @Nullable
    private DockerImage defaultImage;
    @Nullable
    private List<DockerImage> customImages;

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