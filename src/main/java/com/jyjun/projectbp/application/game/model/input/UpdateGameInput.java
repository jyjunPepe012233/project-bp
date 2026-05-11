package com.jyjun.projectbp.application.game.model.input;

public record UpdateGameInput(
        Long gameId,
        String title,
        String description
) {
}
