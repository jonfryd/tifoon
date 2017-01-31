package it.flipb.theapp.plugin;

import it.flipb.theapp.plugin.io.AbstractIoPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class YamlIoPlugin extends AbstractIoPlugin {
    private static final String PROVIDES = "yaml";

    private static final Logger logger = LoggerFactory.getLogger(YamlIoPlugin.class);

    public YamlIoPlugin() {
    }

    @Override
    public boolean supports(final String _s) {
        return PROVIDES.equals(_s);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T load(final InputStream _inputStream, final Class<T> _rootClass) {
        Assert.notNull(_inputStream, "InputStream cannot be null");
        Assert.notNull(_rootClass, "Root class cannot be null");

        final Yaml yaml = new Yaml(new Constructor(_rootClass));

        logger.debug("Loading yaml");

        return (T) yaml.load(_inputStream); // cast is safe
    }

    @Override
    public void save(final OutputStream _outputStream, final Object _object) {
        Assert.notNull(_outputStream, "OutputStream cannot be null");
        Assert.notNull(_object, "Object cannot be null");

        // Hide root bean type
        // http://stackoverflow.com/questions/19246027/how-to-hide-bean-type-in-snakeyaml
        final Representer hideRootTagRepresenter = new Representer();
        hideRootTagRepresenter.addClassTag(_object.getClass(), Tag.MAP);

        final DumperOptions dumperOptions;
        dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

        final Yaml yaml = new Yaml(hideRootTagRepresenter, dumperOptions);

        logger.debug("Saving yaml");

        yaml.dump(_object, new OutputStreamWriter(_outputStream, StandardCharsets.UTF_8));
    }
}
