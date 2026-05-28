package com.jyjun.projectbp.application.catalog.model.input;

import java.io.InputStream;

public record UploadCatalogInput(
        Long patchId,
        InputStream catalogData
) {
}
