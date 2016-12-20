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
        Collection<File> jarFiles = FileUtils.listFiles(new File("plugins/"), new String[]{"jar"}, false);

        for(File jarFile : jarFiles) {
            URL jarURL = new URL("jar:file:" + jarFile.getPath() + "!/BOOT-INF/classes/");
            addURL(jarURL);

            JarFileArchive jarFileArchive = new JarFileArchive(jarFile);
            List<Archive> nestedArchives = jarFileArchive
                    .getNestedArchives(entry -> entry.getName().endsWith(".jar"));

            for(Archive archive : nestedArchives) {
                addURL(archive.getUrl());
            }
        }
    }
}
