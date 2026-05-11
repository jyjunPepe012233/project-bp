package com.jyjun.projectbp.application.game.model.output;

import java.util.UUID;

public record UpdateGameOutput(
        Long id,
        UUID uuid,
        String title,
        String description,
        Long developerId
) {
}
