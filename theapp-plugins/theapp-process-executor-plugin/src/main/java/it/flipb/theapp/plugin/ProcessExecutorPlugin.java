package it.flipb.theapp.plugin;

import it.flipb.theapp.plugin.executer.AbstractExecutorPlugin;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class ProcessExecutorPlugin extends AbstractExecutorPlugin {
    private static final String PROVIDES = "process";

    private static final Logger logger = LoggerFactory.getLogger(ProcessExecutorPlugin.class);

    public ProcessExecutorPlugin() {
    }

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    public byte[] dispatch(final String _command,
                           final String[] _arguments,
                           final String _outputFile) {
        Assert.notNull(_command, "command cannot be null");
        Assert.notNull(_arguments, "arguments cannot be null");
        Assert.notNull(_outputFile, "output file cannot be null");

        String[] commandWithArguments = Stream.concat(Arrays.stream(new String[]{_command}), Arrays.stream(_arguments))
                .toArray(String[]::new);

        try {
            Process myProcess = new ProcessBuilder(commandWithArguments).start();

            if (myProcess.waitFor(30, TimeUnit.SECONDS)) {
                if (myProcess.exitValue() == 0) {
                    final File outputFile = new File(_outputFile);
                    byte outputData[] = FileUtils.readFileToByteArray(outputFile);
                    outputFile.delete();

                    return outputData;
                } else {
                    logger.error("Non-zero exit code executing command: " + _command);
                }
            } else {
                logger.error("Timed out executing command: " + _command);
            }
        } catch (Exception _e) {
            logger.error("Failed to execute command: " + _command, _e);
        }

        return null;
    }
}
