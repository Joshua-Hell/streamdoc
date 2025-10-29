package de.joshua_hell.streamdoc.model;

import de.joshua_hell.streamdoc.model.enums.Direction;

public record Channel(
        String channel,
        Direction direction,
        String payloadClassName
) {
}
