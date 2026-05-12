package com.jyjun.projectbp.application.patch.model.output;

import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;

public record UpdatePatchNoteOutput(
        Long id,
        Long gameId,
        String version,
        PatchPlatform platform,
        String patchNote
) {
}
