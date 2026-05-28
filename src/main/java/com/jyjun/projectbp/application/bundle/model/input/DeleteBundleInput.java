package com.jyjun.projectbp.application.bundle.model.input;

import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;

public record DeleteBundleInput(
        Long gameId,
        PatchPlatform platform,
        String filename
) {
}
