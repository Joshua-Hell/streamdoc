package de.joshua_hell.streamdoc.model;

import java.util.Set;

public record StreamDoc(String projectName, Set<Channel> channels) {
}
