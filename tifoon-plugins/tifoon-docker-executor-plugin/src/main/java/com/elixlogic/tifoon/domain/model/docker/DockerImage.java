package com.elixlogic.tifoon.domain.model.docker;

import com.elixlogic.tifoon.domain.model.configuration.Validator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.annotation.Nullable;

@Data
@NoArgsConstructor
public class DockerImage implements Validator {
    public static final String DEFAULT_COMMAND = "DEFAULT_COMMAND";

    @Nullable
    private String command;
    @Nullable
    private String image;

    DockerImage(final String _image) {
        command = DEFAULT_COMMAND;
        image = _image;
    }

    @Override
    public void validate() {
        Assert.hasLength(command, "command must have length");
        Assert.hasLength(image, "image must have length");
    }
}
