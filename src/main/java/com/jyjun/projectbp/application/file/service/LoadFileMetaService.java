package com.jyjun.projectbp.application.file.service;

import com.jyjun.projectbp.application.file.outbound.FileMetaRepositoryPort;
import com.jyjun.projectbp.domain.filemeta.model.FileMeta;
import org.springframework.stereotype.Component;

@Component
public class LoadFileMetaService {

    private final FileMetaRepositoryPort fileMetaRepositoryPort;

    public LoadFileMetaService(FileMetaRepositoryPort fileMetaRepositoryPort) {
        this.fileMetaRepositoryPort = fileMetaRepositoryPort;
    }

    public FileMeta loadByOriginalName(String originalName) {
        return fileMetaRepositoryPort.findByOriginalName(originalName);
    }
}
