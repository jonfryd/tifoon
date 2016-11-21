package it.flipb.theapp.domain.service.scanning.impl;

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
import com.google.common.collect.Lists;
import it.flipb.theapp.domain.model.scanning.PortRange;
import it.flipb.theapp.domain.model.scanning.PortScannerJob;
import it.flipb.theapp.domain.model.scanning.PortScannerResult;
import it.flipb.theapp.domain.service.scanning.PortScannerService;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
class NmapPortScannerServiceImpl implements PortScannerService {
    private static final String NMAP_DOCKER_IMAGE = "uzyexe/nmap:latest";

    private static final String NMAP_XML_RESULT_FILENAME = "/tmp/nmap_scan_result.xml";

    private static final Logger logger = LoggerFactory.getLogger(NmapPortScannerServiceImpl.class);

    @Override
    public PortScannerResult scan(final PortScannerJob _request) {
        final DockerClient dockerClient = getDockerClient();

        logger.info("Connected to Docker instance.");

        final CreateContainerResponse container = createNmapContainer(dockerClient);

        logger.info("Starting container.");

        dockerClient.startContainerCmd(container.getId()).exec();

        final boolean result = runNmap(dockerClient, container, _request);

        if (!result) {
            logger.warn("Scan was not completed.");
        }

        logger.info("Stopping container.");

        stopAndRemoveContainer(dockerClient, container);

        // TODO: read XML from container, parse XML into PortScannerResult, return result instead of null

        return null;
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

        final String localDockerHost = SystemUtils.IS_OS_WINDOWS ? "//./pipe/docker_engine" : "unix:///var/run/docker.sock";

        final DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost(localDockerHost)
                .build();

        return DockerClientBuilder
                .getInstance(config)
                .build();
    }

    private CreateContainerResponse createNmapContainer(final DockerClient _dockerClient) {
        synchronized(this) {
            if (_dockerClient.listImagesCmd().withImageNameFilter(NMAP_DOCKER_IMAGE).exec().isEmpty()) {
                logger.info("Nmap image not found. Pulling from main repository...");

                boolean success = false;

                try {
                    success = _dockerClient.pullImageCmd(NMAP_DOCKER_IMAGE)
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
                    throw new RuntimeException("Unable to retrieve nmap image");
                }
            } else {
                logger.info("Nmap image found.");
            }
        }

        return _dockerClient
                .createContainerCmd(NMAP_DOCKER_IMAGE)
                .withEntrypoint("/bin/sh")
                .withTty(true)
                .exec();
    }

    private boolean runNmap(final DockerClient _dockerClient,
                            final CreateContainerResponse _container,
                            final PortScannerJob _request) {
        final String[] arguments = createNmapCommandWithArguments(_request);

        final ExecCreateCmdResponse mExecCreateCmdResponse = _dockerClient
                .execCreateCmd(_container.getId())
                .withAttachStdout(true)
                .withCmd(arguments)
                .exec();

        try {
            return _dockerClient
                    .execStartCmd(mExecCreateCmdResponse.getId())
                    .exec(new ExecStartResultCallback() {
                        @Override
                        public void onNext(Frame frame) {
                            System.out.print(new String(frame.getPayload()));
                            super.onNext(frame);
                        }
                    })
                    .awaitCompletion(30, TimeUnit.SECONDS); // TODO: scan times might be a lot high in real life
        } catch (InterruptedException e) {
            // ignore
        }

        return false;
    }

    private String[] createNmapCommandWithArguments(final PortScannerJob _request) {
        final String nmapPortRanges = _request.getPortRanges()
                .stream()
                .map(PortRange::toSingleOrIntervalString)
                .collect(Collectors.joining(","));

        final List<String> targetHosts = _request.getAddresses()
                .stream()
                .map(InetAddress::getHostAddress)
                .collect(Collectors.toList());

        final List<String> argumentsList = Lists.newArrayList("nmap", "-oX", NMAP_XML_RESULT_FILENAME, "-p", nmapPortRanges);
        argumentsList.addAll(targetHosts);

        return argumentsList.toArray(new String[argumentsList.size()]);
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
