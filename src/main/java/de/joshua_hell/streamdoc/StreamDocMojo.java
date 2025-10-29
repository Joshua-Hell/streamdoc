package de.joshua_hell.streamdoc;

import de.joshua_hell.streamdoc.model.Channel;
import de.joshua_hell.streamdoc.model.ChannelBinding;
import de.joshua_hell.streamdoc.model.StreamDoc;
import de.joshua_hell.streamdoc.model.enums.Direction;
import de.joshua_hell.streamdoc.spi.StreamDocWriter;
import io.github.classgraph.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

@Mojo(name = "streamdoc", defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class StreamDocMojo extends AbstractMojo {
    @Parameter(defaultValue = "${project}", readonly = true)
    MavenProject project;

    @Parameter(defaultValue = "${project.build.outputDirectory}", readonly = true)
    File classesDir;

    @Parameter(defaultValue = "${project.build.directory}/streamdoc", readonly = true)
    File outputDir;

    private static final List<ChannelBinding> channelBindings = List.of(
            new ChannelBinding("org.eclipse.microprofile.reactive.messaging.Incoming", null, Direction.INCOMING),
            new ChannelBinding("org.eclipse.microprofile.reactive.messaging.Outgoing", null, Direction.OUTGOING),
            new ChannelBinding("org.eclipse.microprofile.reactive.messaging.Channel", "org.eclipse.microprofile.reactive.messaging.Emitter", Direction.OUTGOING)
    );

    @Override
    public void execute() throws MojoExecutionException {
        Set<Channel> channels = new LinkedHashSet<>();

        try (
                ScanResult scanResult = new ClassGraph()
                        .overrideClasspath(classesDir)
                        .enableClassInfo()
                        .enableMethodInfo()
                        .enableFieldInfo()
                        .enableAnnotationInfo()
                        .ignoreClassVisibility()
                        .ignoreFieldVisibility()
                        .scan()
        ) {
            for (ClassInfo classInfo : scanResult.getAllClasses()) {
                for (MethodInfo methodInfo : classInfo.getMethodInfo()) {
                    for (ChannelBinding channelBinding : channelBindings) {
                        AnnotationInfo annotationInfo = methodInfo.getAnnotationInfo(channelBinding.annotationClassPath());
                        if (null == annotationInfo) continue;

                        String channel = String.valueOf(annotationInfo.getParameterValues().getValue("value"));

                        String payloadClassName = switch (channelBinding.direction()) {
                            case INCOMING -> methodInfo.getParameterInfo()[0].getTypeDescriptor().toString();
                            case OUTGOING -> methodInfo.getTypeDescriptor().getResultType().toString();
                        };

                        channels.add(new Channel(channel, channelBinding.direction(), payloadClassName));
                    }
                }

                for (FieldInfo fieldInfo : classInfo.getFieldInfo()) {
                    for (ChannelBinding channelBinding : channelBindings) {
                        AnnotationInfo annotationInfo = fieldInfo.getAnnotationInfo(channelBinding.annotationClassPath());
                        if (!fieldInfo.getTypeDescriptor().toString().equals(channelBinding.fieldClassPath()) || null == annotationInfo)
                            continue;

                        String channel = String.valueOf(annotationInfo.getParameterValues().getValue("value"));

                        String payloadClassName = fieldInfo.getTypeSignature().toString();

                        channels.add(new Channel(channel, channelBinding.direction(), payloadClassName));
                    }
                }
            }
        }

        try {
            write(new StreamDoc(project.getName(), channels));
        } catch (IOException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    private void write(StreamDoc streamDoc) throws IOException {
        Path outDir = outputDir.toPath();
        Files.createDirectories(outDir);

        getLog().info(String.valueOf(listWriters().size()));

        for (StreamDocWriter writer : listWriters()) {
            String doc = writer.write(streamDoc);
            Files.writeString(outDir.resolve(writer.fileName()), doc);
        }
    }

    private List<StreamDocWriter> listWriters() {
        return ServiceLoader
                .load(StreamDocWriter.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .toList();
    }
}
