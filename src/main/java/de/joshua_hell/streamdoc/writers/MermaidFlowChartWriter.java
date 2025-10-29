package de.joshua_hell.streamdoc.writers;

import de.joshua_hell.streamdoc.model.Channel;
import de.joshua_hell.streamdoc.model.StreamDoc;
import de.joshua_hell.streamdoc.spi.StreamDocWriter;

public class MermaidFlowChartWriter implements StreamDocWriter {
    @Override
    public String fileName() {
        return "streamdoc.mmd";
    }

    @Override
    public String write(StreamDoc streamDoc) {
        String projectName = streamDoc.projectName();
        StringBuilder sb = new StringBuilder();

        sb.append("flowchart TD\n");

        for (Channel channel : streamDoc.channels()) {
            sb.append("\t");
            switch (channel.direction()) {
                case OUTGOING -> sb
                        .append(hexagon(projectName, projectName))
                        .append("-->")
                        .append(channel.channel());
                case INCOMING -> sb
                        .append(channel.channel())
                        .append("-->")
                        .append(hexagon(projectName, projectName));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private String hexagon(final String id, final String text) {
        return id + "{{" + text + "}}";
    }
}
