package it.flipb.theapp.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="application")
public class Application {
    private boolean scanning;

    public boolean isScanning() {
        return scanning;
    }

    public void setScanning(final boolean _scanning) {
        this.scanning = _scanning;
    }

    @Override
    public String toString() {
        return "Application{" +
                "scanning=" + scanning +
                '}';
    }
}