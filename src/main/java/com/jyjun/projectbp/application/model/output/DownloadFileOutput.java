package com.jyjun.projectbp.application.model.output;

import java.io.InputStream;

public record DownloadFileOutput(
        String originalName,
        String storedName,
        long sizeByte,
        InputStream fileData
) {
}
