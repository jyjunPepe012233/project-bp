package com.jyjun.projectbp.application.game.model.input;

public record CreateGameInput(
        Long developerId,
        String title,
        String description
) {
}
