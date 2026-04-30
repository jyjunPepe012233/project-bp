package com.jyjun.projectbp.application.file.service;

import com.jyjun.projectbp.application.file.outbound.FileMetaRepositoryPort;
import com.jyjun.projectbp.domain.filemeta.model.FileMeta;
import org.springframework.stereotype.Component;

@Component
public class CreateFileMetaService {

    private final FileMetaRepositoryPort fileMetaRepositoryPort;
    private final SaveFileService saveFileService;

    public CreateFileMetaService(FileMetaRepositoryPort fileMetaRepositoryPort) {
        this.fileMetaRepositoryPort = fileMetaRepositoryPort;
    }

    public FileMeta create(String originalName, String storedName, long sizeByte) {
        FileMeta fileMeta = new FileMeta(originalName, storedName, sizeByte);
        return fileMetaRepositoryPort.save(fileMeta);
    }
}
