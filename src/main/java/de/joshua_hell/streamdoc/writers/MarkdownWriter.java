package de.joshua_hell.streamdoc.writers;

import de.joshua_hell.streamdoc.model.StreamDoc;
import de.joshua_hell.streamdoc.spi.StreamDocWriter;

public class MarkdownWriter implements StreamDocWriter {
    @Override
    public String fileName() {
        return "streamdoc.md";
    }

    @Override
    public String write(StreamDoc streamDoc) {
        StringBuilder sb = new StringBuilder("# Reactive Messaging Streams\n\n");
        streamDoc.channels().forEach(ch -> {
            sb.append("## ")
                    .append(ch.channel())
                    .append(" ")
                    .append(ch.direction())
                    .append(" ")
                    .append(ch.payloadClassName())
                    .append("\n\n");
        });
        return sb.toString();
    }
}
