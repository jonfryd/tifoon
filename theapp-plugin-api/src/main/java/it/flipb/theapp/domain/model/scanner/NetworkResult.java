package it.flipb.theapp.domain.model.scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.flipb.theapp.domain.model.object.ReflectionObjectTreeAware;
import lombok.*;
import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;
import org.hibernate.type.descriptor.java.SerializableTypeDescriptor;

import javax.persistence.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
@Data
@NoArgsConstructor
public class NetworkResult extends ReflectionObjectTreeAware {
    public static class OpenHostsJsonConverter implements AttributeConverter<ArrayList<OpenHost>, byte[]> {
        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        static {
            JavaTypeDescriptorRegistry.INSTANCE.addDescriptor(new SerializableTypeDescriptor<>(ArrayList.class));
        }

        @Override
        public byte[] convertToDatabaseColumn(final ArrayList<OpenHost> _entityValue) {
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
        public ArrayList<OpenHost> convertToEntityAttribute(final byte[] _databaseValue) {
            if (_databaseValue == null) {
                return null;
            }

            try {
                return OBJECT_MAPPER.readValue(_databaseValue, new TypeReference<List<OpenHost>>(){});
            } catch (IOException _e) {
                throw new RuntimeException("cannot deserialize open hosts from: " + Arrays.toString(_databaseValue), _e);
            }
        }
    }

    @NonNull
    private String description;

    @Convert(converter = OpenHostsJsonConverter.class)
    private ArrayList<OpenHost> openHosts;

    public NetworkResult(@NonNull final String _description,
                         @NonNull final Map<InetAddress, List<Port>> _openPortsMap) {
        description = _description;
        openHosts = new ArrayList<>(
                _openPortsMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey().getHostAddress(), e -> OpenHost.from(e.getKey().getHostAddress(), e.getValue())))
                .values());
    }
}
