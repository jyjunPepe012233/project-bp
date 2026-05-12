package com.jyjun.projectbp.application.patch.model.output;

import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;

import java.time.LocalDateTime;

public record CreatePatchOutput(
        Long id,
        Long gameId,
        String version,
        PatchPlatform platform,
        String patchNote,
        LocalDateTime createdAt
) {
}
