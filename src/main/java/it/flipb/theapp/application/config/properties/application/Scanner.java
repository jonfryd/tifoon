package it.flipb.theapp.application.config.properties.application;

public class Scanner {
    private boolean active;
    private String toolName;

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean _active) {
        this.active = _active;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(final String _toolName) {
        toolName = _toolName;
    }

    @Override
    public String toString() {
        return "Scanner{" +
                "active=" + active +
                ", toolName='" + toolName + '\'' +
                '}';
    }
}
