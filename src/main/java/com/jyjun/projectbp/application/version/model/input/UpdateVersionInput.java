package com.jyjun.projectbp.application.version.model.input;

public record UpdateVersionInput(
        Long gameId,
        Long patchId
) {
}
