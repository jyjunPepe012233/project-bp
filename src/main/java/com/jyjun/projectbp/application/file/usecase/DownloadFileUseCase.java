package com.jyjun.projectbp.application.file.usecase;

import com.jyjun.projectbp.application.file.outbound.FileStoragePort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class DownloadFileUseCase {

    private final FileStoragePort fileStoragePort;

    public DownloadFileUseCase(FileStoragePort fileStoragePort) {
        this.fileStoragePort = fileStoragePort;
    }

    @Transactional
    public InputStream execute(String storedName) {
        return fileStoragePort.load(storedName);
    }
}
