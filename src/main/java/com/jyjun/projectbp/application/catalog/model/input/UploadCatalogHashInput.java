package com.jyjun.projectbp.application.catalog.model.input;

import java.io.InputStream;

public record UploadCatalogHashInput(
        Long patchId,
        InputStream catalogHashData
) {
}
