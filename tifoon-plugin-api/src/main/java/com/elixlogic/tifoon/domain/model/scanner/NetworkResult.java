package com.elixlogic.tifoon.domain.model.scanner;

import com.elixlogic.tifoon.domain.model.object.ReflectionObjectTreeAware;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
import org.hibernate.type.descriptor.java.SerializableTypeDescriptor;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(callSuper = false)
public class NetworkResult extends ReflectionObjectTreeAware implements Serializable {
    public static class OpenHostsJsonConverter implements AttributeConverter<HashMap<String, OpenHost>, byte[]> {
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        static {
            JavaTypeDescriptorRegistry.INSTANCE.addDescriptor(new SerializableTypeDescriptor<>(HashMap.class));
        }

        @Override
        @Nullable
        public byte[] convertToDatabaseColumn(@Nullable final HashMap<String, OpenHost> _entityValue) {
            if (_entityValue == null) {
                return null;
            }

            try {
                return OBJECT_MAPPER.writeValueAsBytes(_entityValue);
            } catch (JsonProcessingException _e) {
                throw new RuntimeException("cannot serialize open hosts: " + _entityValue, _e);
            }
        }

        @Override
        public HashMap<String, OpenHost> convertToEntityAttribute(@Nullable final byte[] _databaseValue) {
            if (_databaseValue == null) {
                return new HashMap<>();
            }

            try {
                return OBJECT_MAPPER.readValue(_databaseValue, new TypeReference<Map<String, OpenHost>>(){});
            } catch (IOException _e) {
                throw new RuntimeException("cannot deserialize open hosts from: " + Arrays.toString(_databaseValue), _e);
            }
        }
    }

    private String networkId;
    private boolean success;

    @NonNull
    @Convert(converter = OpenHostsJsonConverter.class)
    private HashMap<String, OpenHost> openHosts = new HashMap<>();

    public NetworkResult(@NonNull final String _networkId,
                         final boolean _success,
                         @NonNull final Map<InetAddress, List<Port>> _openPortsMap) {
        networkId = _networkId;
        success = _success;
        openHosts = new HashMap<>(_openPortsMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().getHostAddress(), e -> OpenHost.from(e.getValue()))));
    }
}
