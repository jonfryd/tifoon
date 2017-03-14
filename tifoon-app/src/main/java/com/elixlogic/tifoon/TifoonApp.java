package com.elixlogic.tifoon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;
import java.util.Collection;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableScheduling
public class TifoonApp {
    public static void main(String[] args) {
        // provide a comma-separated list of config files due to Spring Boot 1.5 changes to @ConfigurationProperties
        final Collection<File> files = FileUtils.listFiles(new File("config/"), new String[]{"yml"}, false);
        final String configFilesCommaSeparated = files.stream()
                .map(f -> FilenameUtils.removeExtension(f.getName()))
                .collect(Collectors.joining(","));

        // initialize and start the scheduler (see PortScanScheduler)
        new SpringApplicationBuilder(TifoonApp.class)
                .properties("spring.config.name=" + configFilesCommaSeparated)
                .run(args);
    }
}
