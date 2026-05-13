package com.jyjun.projectbp.application.patch.model.input;

import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;

import java.io.InputStream;

public record UploadBundleInput(
        Long gameId,
        PatchPlatform platform,
        String filename,
        InputStream data
) {
}
