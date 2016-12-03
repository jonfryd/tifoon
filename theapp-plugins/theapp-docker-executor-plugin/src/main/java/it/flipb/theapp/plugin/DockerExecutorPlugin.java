package it.flipb.theapp.plugin;

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
import it.flipb.theapp.domain.model.docker.DockerConfiguration;
import it.flipb.theapp.domain.model.docker.DockerImage;
import it.flipb.theapp.plugin.executer.AbstractExecutorPlugin;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DockerExecutorPlugin extends AbstractExecutorPlugin {
    private static final String PROVIDES = "docker";

    private static final Logger logger = LoggerFactory.getLogger(DockerExecutorPlugin.class);

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
    public byte[] dispatch(final String _command,
                           final String[] _arguments,
                           final String _outputFile) {
        Assert.notNull(_command, "command cannot be null");
        Assert.notNull(_arguments, "arguments cannot be null");
        Assert.notNull(_outputFile, "output file cannot be null");

        final DockerClient dockerClient = getDockerClient();

        logger.debug("Connected to Docker instance.");

        final DockerImage customDockerImage = findDockerImage(_command);
        final CreateContainerResponse container = createContainer(dockerClient, customDockerImage.getImage());

        logger.debug("Starting container.");

        dockerClient.startContainerCmd(container.getId()).exec();

        String[] commandWithArguments = Stream.concat(Arrays.stream(new String[]{_command}), Arrays.stream(_arguments))
                .toArray(String[]::new);

        final boolean result = runCommand(dockerClient, container, commandWithArguments);

        byte[] data = null;

        if (!result) {
            logger.warn("Command did not complete normally.");
        } else {
            logger.debug("Reading file from container.");

            data = readFileFromContainer(dockerClient, container, _outputFile);
        }

        logger.debug("Stopping container.");

        stopAndRemoveContainer(dockerClient, container);

        return data;
    }

    @NotNull
    private DockerImage findDockerImage(final String _command) {
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

    private CreateContainerResponse createContainer(final DockerClient _dockerClient,
                                                    final String _dispatcher) {
        synchronized(this) {
            if (_dockerClient.listImagesCmd().withImageNameFilter(_dispatcher).exec().isEmpty()) {
                logger.info("Image '" + _dispatcher + "' not found. Please wait while pulling from main repository...");

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
