package de.joshua_hell.streamdoc;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;

@Mojo(name = "clean", defaultPhase = LifecyclePhase.CLEAN)
public class StreamDocCleanMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project.build.directory}/streamdoc", readonly = true)
    File outputDir;

    @Override
    public void execute() throws MojoExecutionException {
        try {
            FileUtils.deleteDirectory(outputDir);
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
