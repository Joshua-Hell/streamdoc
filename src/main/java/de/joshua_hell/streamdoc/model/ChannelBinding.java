package de.joshua_hell.streamdoc.model;

import de.joshua_hell.streamdoc.model.enums.Direction;

public record ChannelBinding(
        String annotationClassPath,
        String fieldClassPath,
        Direction direction
) {
}
