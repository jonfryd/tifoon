package it.flipb.theapp.domain.model.docker;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DockerImage {
    public static final String DEFAULT_COMMAND = "DEFAULT_COMMAND";

    private String command;
    private String image;

    DockerImage(final String _image) {
        command = DEFAULT_COMMAND;
        image = _image;
    }
}
