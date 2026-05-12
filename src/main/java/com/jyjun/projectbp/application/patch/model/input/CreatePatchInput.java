package com.jyjun.projectbp.application.patch.model.input;

import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;

public record CreatePatchInput(
        Long gameId,
        String version,
        PatchPlatform platform,
        String patchNote
) {
}
