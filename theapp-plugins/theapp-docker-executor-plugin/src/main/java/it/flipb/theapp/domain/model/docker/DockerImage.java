package it.flipb.theapp.domain.model.docker;

public class DockerImage {
    public static final String DEFAULT_COMMAND = "DEFAULT_COMMAND";

    private String command;
    private String image;

    public DockerImage() {
    }

    DockerImage(final String _image) {
        command = DEFAULT_COMMAND;
        image = _image;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(final String _command) {
        command = _command;
    }

    public String getImage() {
        return image;
    }

    public void setImage(final String _image) {
        image = _image;
    }

    @Override
    public String toString() {
        return "DockerImage{" +
                "command='" + command + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
