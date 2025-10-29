package de.joshua_hell.streamdoc.writers;

import de.joshua_hell.streamdoc.model.StreamDoc;
import de.joshua_hell.streamdoc.spi.StreamDocWriter;
import tools.jackson.databind.ObjectMapper;

public class JsonWriter implements StreamDocWriter {
    @Override
    public String fileName() {
        return "streamdoc.json";
    }

    @Override
    public String write(StreamDoc streamDoc) {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(streamDoc);
    }
}
