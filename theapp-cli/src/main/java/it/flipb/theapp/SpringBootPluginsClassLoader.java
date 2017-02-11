package it.flipb.theapp;

import org.apache.commons.io.FileUtils;
import org.springframework.boot.loader.LaunchedURLClassLoader;
import org.springframework.boot.loader.archive.Archive;
import org.springframework.boot.loader.archive.JarFileArchive;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

public class SpringBootPluginsClassLoader extends LaunchedURLClassLoader {
    public SpringBootPluginsClassLoader(final ClassLoader parent) throws IOException {
        super(new URL[]{}, parent);

        // find all jar plugins
        final Collection<File> jarFiles = FileUtils.listFiles(new File("plugins/"), new String[]{"jar"}, false);

        for(final File jarFile : jarFiles) {
            // add the jar's own classes to classpath
            final URL jarURL = new URL("jar:file:" + jarFile.getPath() + "!/BOOT-INF/classes/");
            addURL(jarURL);

            final JarFileArchive jarFileArchive = new JarFileArchive(jarFile);
            final List<Archive> nestedArchives = jarFileArchive
                    .getNestedArchives(entry -> entry.getName().endsWith(".jar"));

            for(final Archive archive : nestedArchives) {
                // add all bundled dependencies to classpath
                addURL(archive.getUrl());
            }
        }
    }
}
