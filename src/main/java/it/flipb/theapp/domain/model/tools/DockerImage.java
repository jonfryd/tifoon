package it.flipb.theapp.domain.model.tools;

/**
 * Created by jon on 22/11/2016.
 */
public class DockerImage {
    private String command;
    private String image;

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
