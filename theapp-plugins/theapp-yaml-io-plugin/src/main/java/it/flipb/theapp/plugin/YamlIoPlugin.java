package it.flipb.theapp.plugin;

import it.flipb.theapp.plugin.io.AbstractIoPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Slf4j
public class YamlIoPlugin extends AbstractIoPlugin {
    private static final String PROVIDES = "yaml";

    public YamlIoPlugin() {
    }

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T> T load(@NonNull final InputStream _inputStream,
                      @NonNull final Class<T> _rootClass) {
        final Yaml yaml = new Yaml(new Constructor(_rootClass));

        log.debug("Loading yaml");

        return (T) yaml.load(_inputStream); // cast is safe
    }

    @Override
    public void save(@NonNull final OutputStream _outputStream,
                     @NonNull final Object _object) {
        // Hide root bean type
        // http://stackoverflow.com/questions/19246027/how-to-hide-bean-type-in-snakeyaml
        final Representer hideRootTagRepresenter = new Representer();
        hideRootTagRepresenter.addClassTag(_object.getClass(), Tag.MAP);

        final DumperOptions dumperOptions;
        dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        final Yaml yaml = new Yaml(hideRootTagRepresenter, dumperOptions);

        log.debug("Saving yaml");

        yaml.dump(_object, new OutputStreamWriter(_outputStream, StandardCharsets.UTF_8));
    }
}
