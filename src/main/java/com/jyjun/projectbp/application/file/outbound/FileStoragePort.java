package com.jyjun.projectbp.application.file.outbound;

import java.io.InputStream;

public interface FileStoragePort {

    // long: 파일의 크기 (byte)
    long save(String storedName, InputStream fileData);

    InputStream load(String storedName);

    void delete(String storedName);
}
