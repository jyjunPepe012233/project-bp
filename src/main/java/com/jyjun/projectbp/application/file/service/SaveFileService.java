package com.jyjun.projectbp.application.file.service;

import com.jyjun.projectbp.application.file.outbound.FileStoragePort;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class SaveFileService {

    private final FileStoragePort fileStoragePort;

    public SaveFileService(FileStoragePort fileStoragePort) {
        this.fileStoragePort = fileStoragePort;
    }

    // long: 파일의 크기 (byte)
    public long save(String storedName, InputStream fileData) {
        return fileStoragePort.save(storedName, fileData);
    }
}
