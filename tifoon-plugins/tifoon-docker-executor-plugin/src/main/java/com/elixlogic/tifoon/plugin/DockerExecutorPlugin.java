package com.elixlogic.tifoon.plugin;

import com.elixlogic.tifoon.domain.model.docker.DockerImage;
import com.elixlogic.tifoon.plugin.executer.AbstractExecutorPlugin;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.PullResponseItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.WaitContainerResultCallback;
import com.google.common.base.MoreObjects;
import com.elixlogic.tifoon.domain.model.docker.DockerConfiguration;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class DockerExecutorPlugin extends AbstractExecutorPlugin {
    private static final String PROVIDES = "docker";

    private final DockerConfiguration dockerConfiguration;

    private final Map<String, DockerImage> customDockerImageMap;

    public DockerExecutorPlugin(final DockerConfiguration _dockerConfiguration) {
        dockerConfiguration = _dockerConfiguration;

        customDockerImageMap = dockerConfiguration.getCustomImages()
                .stream()
                .collect(Collectors.toMap(DockerImage::getCommand, i -> i));
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
        final DockerClient dockerClient = getDockerClient();

        log.debug("Connected to Docker instance.");

        final DockerImage customDockerImage = findDockerImage(_command);
        final CreateContainerResponse container = createContainer(dockerClient, customDockerImage.getImage());

        log.debug("Starting container.");

        dockerClient.startContainerCmd(container.getId()).exec();

        final String[] commandWithArguments = Stream.concat(Arrays.stream(new String[]{_command}), Arrays.stream(_arguments))
                .toArray(String[]::new);

        final String formattedCommand = Stream
                .of(commandWithArguments)
                .collect(Collectors.joining(" ","[","]"));
        log.info("Docker executing: " + formattedCommand);

        final boolean result = runCommand(dockerClient, container, commandWithArguments);

        byte[] data = null;

        if (!result) {
            log.warn("Command did not complete normally.");
        } else {
            log.debug("Reading file from container.");

            data = readFileFromContainer(dockerClient, container, _outputFile);
        }

        log.debug("Stopping container.");

        stopAndRemoveContainer(dockerClient, container);

        return data;
    }

    private DockerImage findDockerImage(@NonNull final String _command) {
        Assert.hasLength(_command, "command must have length");

        return MoreObjects.firstNonNull(customDockerImageMap.get(_command), dockerConfiguration.getDefaultImage());
    }

    private DockerClient getDockerClient() {
        /*
        TLS connection: ...

        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.99.100:2376")
                .withDockerTlsVerify(true)
                .withDockerCertPath("/Users/jon/.docker/machine/machines/default")
                .build();
        */

        final String localDockerHost = SystemUtils.IS_OS_WINDOWS ? "tcp://localhost:2375" : "unix:///var/run/docker.sock";

        final DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost(localDockerHost)
                .build();

        return DockerClientBuilder
                .getInstance(config)
                .build();
    }

    private CreateContainerResponse createContainer(@NonNull final DockerClient _dockerClient,
                                                    @NonNull final String _dispatcher) {
        synchronized(this) {
            if (_dockerClient.listImagesCmd().withImageNameFilter(_dispatcher).exec().isEmpty()) {
                log.info("Image '" + _dispatcher + "' not found. Please wait while pulling from main repository...");

                boolean success = false;

                try {
                    success = _dockerClient.pullImageCmd(_dispatcher)
                            .exec(new PullImageResultCallback() {
                                @Override
                                public void onNext(PullResponseItem item) {
                                    //System.out.println("" + item);
                                    super.onNext(item);
                                }
                            })
                            .awaitCompletion(5, TimeUnit.MINUTES);
                } catch (InterruptedException _e) {
                    // ignore
                }

                if (!success) {
                    throw new RuntimeException("Unable to retrieve image:" + _dispatcher);
                }
            } else {
                log.debug("Image found.");
            }
        }

        return _dockerClient
                .createContainerCmd(_dispatcher)
                .withEntrypoint("/bin/sh")
                .withTty(true)
                .exec();
    }

    private boolean runCommand(@NonNull final DockerClient _dockerClient,
                               @NonNull final CreateContainerResponse _container,
                               @NonNull final String[] _commandWithArguments) {
        final ExecCreateCmdResponse mExecCreateCmdResponse = _dockerClient
                .execCreateCmd(_container.getId())
                .withAttachStdout(true)
                .withCmd(_commandWithArguments)
                .exec();

        try {
            return _dockerClient
                    .execStartCmd(mExecCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            //System.out.print(new String(frame.getPayload()));
                            super.onNext(frame);
                        }
                    })
                    .awaitCompletion(30, TimeUnit.SECONDS); // TODO: scan times might be a lot high in real life
        } catch (InterruptedException e) {
            // ignore
        }

        return false;
    }

    @Nullable
    private byte[] readFileFromContainer(@NonNull final DockerClient _dockerClient,
                                         @NonNull final CreateContainerResponse _container,
                                         @NonNull final String _outputFile) {
        final InputStream fileStream =_dockerClient
                .copyArchiveFromContainerCmd(_container.getId(), _outputFile)
                .exec();
        final TarArchiveInputStream tarIn = new TarArchiveInputStream(fileStream);

        try {
            if (tarIn.getNextEntry() == null) {
                log.error("No entry in tar archive");
                return null;
            }

            return IOUtils.toByteArray(tarIn);
        } catch (IOException _e) {
            log.error("Could not read file", _e);
            return null;
        }
    }

    private void stopAndRemoveContainer(@NonNull final DockerClient _dockerClient,
                                        @NonNull final CreateContainerResponse _container) {
        _dockerClient
                .stopContainerCmd(_container.getId())
                .withTimeout(0)
                .exec();

        try {
            _dockerClient
                    .waitContainerCmd(_container.getId())
                    .exec(new WaitContainerResultCallback())
                    .awaitCompletion();
        } catch (InterruptedException e) {
            // ignore
        }

        _dockerClient
                .removeContainerCmd(_container.getId())
                .exec();
    }
}
