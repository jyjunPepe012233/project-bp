package com.jyjun.projectbp.application.patch.model.input;

import java.io.InputStream;

public record UploadBundleInput(
        Long patchId,
        String filename,
        InputStream data
) {
}
