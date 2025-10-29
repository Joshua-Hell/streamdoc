package de.joshua_hell.streamdoc.spi;

import de.joshua_hell.streamdoc.model.StreamDoc;

public interface StreamDocWriter {
    String fileName();

    String write(StreamDoc streamDoc);
}
