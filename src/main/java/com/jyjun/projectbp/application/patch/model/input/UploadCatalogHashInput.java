package com.jyjun.projectbp.application.patch.model.input;

import java.io.InputStream;

public record UploadCatalogHashInput(
        Long patchId,
        String catalogHashFilename,
        InputStream catalogHashData
) {
}
