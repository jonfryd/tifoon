package com.elixlogic.tifoon.plugin;

import com.elixlogic.tifoon.plugin.io.AbstractIoPlugin;
import com.elixlogic.tifoon.plugin.io.ListProperty;
import com.elixlogic.tifoon.plugin.io.MapProperty;
import com.google.common.collect.ImmutableList;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.introspector.BeanAccess;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.representer.Representer;

import javax.annotation.Nullable;
import java.beans.IntrospectionException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class YamlIoPlugin extends AbstractIoPlugin {
    private static final String PROVIDES = "yaml";
    private static final List<String> EXTENSIONS_HANDLED = ImmutableList.of("yml", PROVIDES);

    private static class SkipEmptyRepresenter extends Representer {
        @Override
        @Nullable
        protected NodeTuple representJavaBeanProperty(final Object _javaBean,
                                                      final Property _property,
                                                      final Object _propertyValue,
                                                      final Tag _customTag) {
            final NodeTuple tuple = super.representJavaBeanProperty(_javaBean, _property, _propertyValue, _customTag);
            final Node valueNode = tuple.getValueNode();

            if (Tag.NULL.equals(valueNode.getTag())) {
                return null;// skip 'null' values
            }
            if (valueNode instanceof CollectionNode) {
                if (Tag.SEQ.equals(valueNode.getTag())) {
                    final SequenceNode seq = (SequenceNode) valueNode;
                    if (seq.getValue().isEmpty()) {
                        return null;// skip empty lists
                    }
                }
                if (Tag.MAP.equals(valueNode.getTag()) && valueNode instanceof MappingNode) {
                    final MappingNode seq = (MappingNode) valueNode;
                    if (seq.getValue().isEmpty()) {
                        return null;// skip empty maps
                    }
                }
            }
            return tuple;
        }
    }

    private static class UnsortedPropertyUtils extends PropertyUtils {
        @Override
        protected Set<Property> createPropertySet(final Class<?> _type,
                                                  final BeanAccess _beanAccess) throws IntrospectionException {
            return new LinkedHashSet<>(getPropertiesMap(_type, BeanAccess.FIELD).values());
        }
    }

    public YamlIoPlugin() {
    }

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    @Nullable
    public <T> T load(@NonNull final InputStream _inputStream,
                      @NonNull final Class<T> _rootClass,
                      @NonNull final List<ListProperty> _listProperties,
                      @NonNull final List<MapProperty> _mapProperties) {
        final Constructor constructor = new Constructor(_rootClass);

        for(final ListProperty listProperty : _listProperties) {
            final TypeDescription typeDescription = new TypeDescription(listProperty.getTargetClazz());
            typeDescription.putListPropertyType(listProperty.getProperty(), listProperty.getType());

            constructor.addTypeDescription(typeDescription);
        }
        for(final MapProperty mapProperty : _mapProperties) {
            final TypeDescription typeDescription = new TypeDescription(mapProperty.getTargetClazz());
            typeDescription.putMapPropertyType(mapProperty.getProperty(), mapProperty.getKey(), mapProperty.getValue());

            constructor.addTypeDescription(typeDescription);
        }

        final Yaml yaml = new Yaml(constructor);

        log.debug("Loading yaml");

        return yaml.loadAs(_inputStream, _rootClass);
    }

    @Override
    public void save(@NonNull final OutputStream _outputStream,
                     @NonNull final Object _object,
                     @NonNull final List<Class<?>> _asStringClasses) {
        // Hide root bean type
        // http://stackoverflow.com/questions/19246027/how-to-hide-bean-type-in-snakeyaml
        final Representer hideRootTagRepresenter = new SkipEmptyRepresenter();
        hideRootTagRepresenter.setPropertyUtils(new UnsortedPropertyUtils());
        hideRootTagRepresenter.addClassTag(_object.getClass(), Tag.MAP);

        // deeply entrenched enums need to explicitly tagged as strings :-/
        for(final Class<?> clazz : _asStringClasses) {
            hideRootTagRepresenter.addClassTag(clazz, Tag.STR);
        }

        final DumperOptions dumperOptions;
        dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        final Yaml yaml = new Yaml(hideRootTagRepresenter, dumperOptions);

        log.debug("Saving yaml");

        yaml.dump(_object, new OutputStreamWriter(_outputStream, StandardCharsets.UTF_8));
    }

    @Override
    public String getDefaultFileExtension() {
        return EXTENSIONS_HANDLED.get(0);
    }

    @Override
    public List<String> getFileExtensionsHandled() {
        return EXTENSIONS_HANDLED;
    }
}
