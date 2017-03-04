package com.elixlogic.tifoon.domain.model.scanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Java6Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

import static org.assertj.core.api.Java6Assertions.assertThat;

@JsonTest
public class NetworkResultTest {
    private JacksonTester<List<OpenHost>> json;

    @Before
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    public void testJsonConverterWithOpenHosts() throws IOException {
        final Port port1 = Port.from(Protocol.TCP, 23);
        final Port port2 = Port.from(Protocol.TCP, 132);
        final Port port3 = Port.from(Protocol.TCP, 1234);

        final Map<InetAddress, List<Port>> inetAddressPortMap = new HashMap<>();
        inetAddressPortMap.put(InetAddress.getByName("1.5.3.6"), Arrays.asList(port1, port2));
        inetAddressPortMap.put(InetAddress.getByName("35.74.52.5"), Collections.singletonList(port3));

        final NetworkResult networkResult = new NetworkResult("test", inetAddressPortMap);

        final ArrayList<OpenHost> openHosts = networkResult.getOpenHosts();

        Java6Assertions.assertThat(openHosts).as("openHosts should not be empty").isNotEmpty();

        testJsonSerializationDeserialization(openHosts);
    }

    @Test
    public void testJsonConverterWithNoOpenHosts() throws IOException {
        final NetworkResult networkResult = new NetworkResult("test", Collections.EMPTY_MAP);

        final ArrayList<OpenHost> openHosts = networkResult.getOpenHosts();

        Java6Assertions.assertThat(openHosts).as("openHosts should be empty").isEmpty();

        testJsonSerializationDeserialization(openHosts);
    }

    private void testJsonSerializationDeserialization(final ArrayList<OpenHost> _openHosts) throws IOException {
        final NetworkResult.OpenHostsJsonConverter openHostsJsonConverter = new NetworkResult.OpenHostsJsonConverter();
        final byte[] serializedOpenHosts = openHostsJsonConverter.convertToDatabaseColumn(_openHosts);

        Assertions.assertThat(serializedOpenHosts).as("serialized result should not be null").isNotNull();

        assertThat(json.write(_openHosts)).as("manually serialized JSON should be identical to JPA converter JSON").isEqualToJson(serializedOpenHosts);

        final List<OpenHost> deseralizedOpenHosts = openHostsJsonConverter.convertToEntityAttribute(serializedOpenHosts);

        Assertions.assertThat(deseralizedOpenHosts).as("deserialized result should not be null").isNotNull();

        assertThat(json.parse(serializedOpenHosts)).as("manually deserialized JSON should be identical to JPA converter result").isEqualTo(deseralizedOpenHosts);

        Assertions.assertThat(_openHosts).as("original and deserialized open hosts must be equal").isEqualTo(deseralizedOpenHosts);
    }

    @Test(expected = NullPointerException.class)
    public void throwsWhenSettingNullOpenHosts() throws IOException {
        final NetworkResult networkResult = new NetworkResult("test", Collections.EMPTY_MAP);
        networkResult.setOpenHosts(null);
    }
}
