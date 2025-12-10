package org.project.model.player;

public record Player(
        String name,
        String team,
        Position position,
        int height,
        int weight,
        double age
) {
}
