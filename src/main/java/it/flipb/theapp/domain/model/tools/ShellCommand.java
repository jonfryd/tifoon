package it.flipb.theapp.domain.model.tools;

/**
 * Created by jon on 22/11/2016.
 */
public class ShellCommand {
    private String command;
    private String path;

    public String getCommand() {
        return command;
    }

    public void setCommand(final String _command) {
        command = _command;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String _path) {
        path = _path;
    }

    @Override
    public String toString() {
        return "ShellCommand{" +
                "command='" + command + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
