package com.jyjun.projectbp.application.file.usecase;

import com.jyjun.projectbp.application.file.outbound.FileMetaRepositoryPort;
import com.jyjun.projectbp.application.file.outbound.FileStoragePort;
import com.jyjun.projectbp.application.model.output.DownloadFileOutput;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DownloadFileUseCase {

    private final FileStoragePort fileStoragePort;
    private final FileMetaRepositoryPort fileMetaRepositoryPort;

    public DownloadFileUseCase(FileStoragePort fileStoragePort, FileMetaRepositoryPort fileMetaRepositoryPort) {
        this.fileStoragePort = fileStoragePort;
        this.fileMetaRepositoryPort = fileMetaRepositoryPort;
    }

    @Transactional
    public DownloadFileOutput execute(String originalName) {
        var fileMeta = fileMetaRepositoryPort.findByOriginalName(originalName);
        return new DownloadFileOutput(
                fileMeta.getOriginalName(),
                fileMeta.getStoredName(),
                fileMeta.getSizeByte(),
                fileStoragePort.load(fileMeta.getStoredName())
        );
    }
}
