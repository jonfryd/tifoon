package com.elixlogic.tifoon.plugin;

import com.elixlogic.tifoon.plugin.executer.AbstractExecutorPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ProcessExecutorPlugin extends AbstractExecutorPlugin {
    private static final String PROVIDES = "process";

    public ProcessExecutorPlugin() {
    }

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    @Nullable
    public byte[] dispatch(@NonNull final String _command,
                           @NonNull final String[] _arguments,
                           @NonNull final String _outputFile) {
        final String[] commandWithArguments = Stream.concat(Arrays.stream(new String[]{_command}), Arrays.stream(_arguments))
                .toArray(String[]::new);

        try {
            final String formattedCommand = Stream
                    .of(commandWithArguments)
                    .collect(Collectors.joining(" ","[","]"));
            log.info("Executing process: " + formattedCommand);

            final Process process = new ProcessBuilder(commandWithArguments).start();

            if (process.waitFor(30, TimeUnit.SECONDS)) {
                if (process.exitValue() == 0) {
                    final File outputFile = new File(_outputFile);

                    log.debug("Reading output produced by command");
                    byte data[] = FileUtils.readFileToByteArray(outputFile);

                    log.debug("Deleting file produced by command");
                    final boolean deletionSuccessful = outputFile.delete();

                    if (!deletionSuccessful) {
                        log.warn("Failed to delete temporary file: %s", outputFile.toPath());
                    }

                    return data;
                } else {
                    log.error(String.format("Non-zero exit code (%d) executing command: %s", process.exitValue(), _command));
                }
            } else {
                log.error("Timed out executing command: " + _command);
            }
        } catch (Exception _e) {
            log.error("Failed to execute command: " + _command, _e);
        }

        return null;
    }
}
