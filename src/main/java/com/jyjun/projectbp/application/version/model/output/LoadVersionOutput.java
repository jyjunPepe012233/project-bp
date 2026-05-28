package com.jyjun.projectbp.application.version.model.output;

public record LoadVersionOutput(
        Long gameId,
        Long patchId,
        String version
) {
}
