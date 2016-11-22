package it.flipb.theapp.infrastructure.service.dispatcher.impl;

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
import it.flipb.theapp.infrastructure.service.dispatcher.CommandDispatcher;
import it.flipb.theapp.infrastructure.repository.command.CommandRepository;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
public class DockerCommandDispatcherImpl implements CommandDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(DockerCommandDispatcherImpl.class);

    private final CommandRepository commandRepository;

    @Autowired
    public DockerCommandDispatcherImpl(final CommandRepository _commandRepository) {
        commandRepository = _commandRepository;
    }

    @Override
    public byte[] dispatch(final String _command,
                           final String[] _arguments,
                           final String _outputFile) {
        Assert.notNull(_command, "command cannot be null");
        Assert.notNull(_arguments, "arguments cannot be null");
        Assert.notNull(_outputFile, "output file cannot be null");

        final String image = commandRepository.findDockerImage(_command).getImage();
        Assert.notNull(image, "no Docker image not found for command: " + _command);

        final DockerClient dockerClient = getDockerClient();

        logger.debug("Connected to Docker instance.");

        final CreateContainerResponse container = createContainer(dockerClient, image);

        logger.debug("Starting container.");

        dockerClient.startContainerCmd(container.getId()).exec();

        String[] commandWithArguments = Stream.concat(Arrays.stream(new String[]{_command}), Arrays.stream(_arguments))
                .toArray(String[]::new);

        final boolean result = runCommand(dockerClient, container, commandWithArguments);

        if (!result) {
            logger.warn("Command did not complete normally.");
        }

        logger.debug("Reading file from container.");

        byte[] data = readFileFromContainer(dockerClient, container, _outputFile);

        logger.debug("Stopping container.");

        stopAndRemoveContainer(dockerClient, container);

        return data;
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

    private CreateContainerResponse createContainer(final DockerClient _dockerClient,
                                                    final String _dispatcher) {
        synchronized(this) {
            if (_dockerClient.listImagesCmd().withImageNameFilter(_dispatcher).exec().isEmpty()) {
                logger.debug("Image '" + _dispatcher + " not found. Pulling from main repository...");

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
                            .awaitCompletion(2, TimeUnit.MINUTES);
                } catch (InterruptedException _e) {
                    // ignore
                }

                if (!success) {
                    throw new RuntimeException("Unable to retrieve image:" + _dispatcher);
                }
            } else {
                logger.debug("Image found.");
            }
        }

        return _dockerClient
                .createContainerCmd(_dispatcher)
                .withEntrypoint("/bin/sh")
                .withTty(true)
                .exec();
    }

    private boolean runCommand(final DockerClient _dockerClient,
                               final CreateContainerResponse _container,
                               final String[] _commandWithArguments) {
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

    private byte[] readFileFromContainer(final DockerClient _dockerClient, final CreateContainerResponse _container, final String _outputFile) {
        final InputStream fileStream =_dockerClient
                .copyArchiveFromContainerCmd(_container.getId(), _outputFile)
                .exec();
        final TarArchiveInputStream tarIn = new TarArchiveInputStream(fileStream);

        try {
            if (tarIn.getNextEntry() == null) {
                logger.error("No entry in tar archive");
                return null;
            }

            return IOUtils.toByteArray(tarIn);
        } catch (IOException _e) {
            logger.error("Could not read file", _e);
            return null;
        }
    }

    private void stopAndRemoveContainer(final DockerClient _dockerClient, final CreateContainerResponse _container) {
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
