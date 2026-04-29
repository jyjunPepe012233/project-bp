package com.jyjun.projectbp.application.file.model.output;

import java.io.InputStream;

public record DownloadFileOutput(
        String originalName,
        String storedName,
        long sizeByte,
        InputStream fileData
) {
}
