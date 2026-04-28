package com.jyjun.projectbp.application.file.usecase;

import com.jyjun.projectbp.application.file.outbound.FileMetaRepositoryPort;
import com.jyjun.projectbp.application.file.outbound.FileStoragePort;
import com.jyjun.projectbp.domain.filemeta.model.FileMeta;
import com.jyjun.projectbp.domain.filemeta.service.GenerateFileStoreNameService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.UUID;

@Service
public class UploadFileUseCase {

    private final FileStoragePort fileStoragePort;
    private final GenerateFileStoreNameService generateFileStoreNameService;
    private final FileMetaRepositoryPort fileMetaRepositoryPort;

    public UploadFileUseCase(FileStoragePort fileStoragePort, GenerateFileStoreNameService generateFileStoreNameService, FileMetaRepositoryPort fileMetaRepositoryPort) {
        this.fileStoragePort = fileStoragePort;
        this.generateFileStoreNameService = generateFileStoreNameService;
        this.fileMetaRepositoryPort = fileMetaRepositoryPort;
    }

    @Transactional
    public FileMeta execute(String originalName, InputStream fileData) {

        // 저장 이름 사전 생성 (스토리지 저장 전에 필요)
        String storedName = generateFileStoreNameService.generate(originalName);

        // 스토리지에 스트리밍으로 저장, 실제 기록된 바이트 수 반환
        long sizeByte = fileStoragePort.save(storedName, fileData);

        // 실제 크기로 메타 정보 생성 및 저장
        FileMeta fileMeta = new FileMeta(originalName, storedName, sizeByte);
        return fileMetaRepositoryPort.save(fileMeta);
    }
}
