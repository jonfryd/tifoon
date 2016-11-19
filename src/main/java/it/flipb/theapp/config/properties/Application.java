package it.flipb.theapp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="application")
public class Application {
    private boolean logging;
    private boolean scanning;

    public boolean isLogging() {
        return logging;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public boolean isScanning() {
        return scanning;
    }

    public void setScanning(boolean scanning) {
        this.scanning = scanning;
    }

    @Override
    public String toString() {
        return "Application{" +
            "logging=" + logging +
            ", scanning=" + scanning +
            '}';
    }
}