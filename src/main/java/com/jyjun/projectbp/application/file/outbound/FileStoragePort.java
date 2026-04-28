package com.jyjun.projectbp.application.file.outbound;

import java.io.InputStream;

public interface FileStoragePort {

    long save(String storedName, InputStream fileData);

    InputStream load(String storedName);

    void delete(String storedName);
}
