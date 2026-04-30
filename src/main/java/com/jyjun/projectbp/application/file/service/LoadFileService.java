package com.jyjun.projectbp.application.file.service;

import com.jyjun.projectbp.application.file.outbound.FileStoragePort;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class LoadFileService {

    private final FileStoragePort fileStoragePort;

    public LoadFileService(FileStoragePort fileStoragePort) {
        this.fileStoragePort = fileStoragePort;
    }

    public InputStream load(String storedName) {
        return fileStoragePort.load(storedName);
    }
}
