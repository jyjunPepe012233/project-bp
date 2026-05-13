package com.jyjun.projectbp.application.patch.model.input;

import java.io.InputStream;

public record UploadCatalogInput(
        Long patchId,
        InputStream catalogData
) {
}
