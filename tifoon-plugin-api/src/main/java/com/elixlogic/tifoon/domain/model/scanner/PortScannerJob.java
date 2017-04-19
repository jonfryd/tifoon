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
import java.util.*;

@Embeddable
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PortScannerJob extends ReflectionObjectTreeAware implements Serializable {
    private static abstract class ListConverter implements AttributeConverter<ArrayList<Serializable>, byte[]> {
        static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        static {
            JavaTypeDescriptorRegistry.INSTANCE.addDescriptor(new SerializableTypeDescriptor<>(ArrayList.class));
        }

        @Override
        @Nullable
        public byte[] convertToDatabaseColumn(@Nullable final ArrayList<Serializable> _entityValue) {
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
        public ArrayList<Serializable> convertToEntityAttribute(@Nullable final byte[] _databaseValue) {
            if (_databaseValue == null) {
                return new ArrayList<>();
            }

            try {
                return OBJECT_MAPPER.readValue(_databaseValue, createTypeReference());
            } catch (IOException _e) {
                throw new RuntimeException("cannot deserialize open hosts from: " + Arrays.toString(_databaseValue), _e);
            }
        }

        protected abstract TypeReference createTypeReference();
    }

    public static class HostConverter extends ListConverter
    {
        @Override
        protected TypeReference createTypeReference() {
            return new TypeReference<ArrayList<Host>>(){};
        }
    }

    public static class PortRangeConverter extends ListConverter
    {
        @Override
        protected TypeReference createTypeReference() {
            return new TypeReference<ArrayList<PortRange>>(){};
        }
    }

    private String networkId;
    @Convert(converter = HostConverter.class)
    private ArrayList<Host> hosts;
    @Convert(converter = PortRangeConverter.class)
    private ArrayList<PortRange> portRanges;
    private String jobHash;

    public PortScannerJob(@NonNull final String _networkId,
                          @NonNull final List<Host> _hosts,
                          @NonNull final List<PortRange> _portRanges) {
        networkId = _networkId;
        hosts = new ArrayList<>(_hosts);
        portRanges = new ArrayList<>(_portRanges);
        jobHash = Integer.toHexString(hashCode());
    }
}
