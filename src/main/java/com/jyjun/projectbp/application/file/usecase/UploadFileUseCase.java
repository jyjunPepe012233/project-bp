package com.jyjun.projectbp.application.file.usecase;

import com.jyjun.projectbp.application.file.outbound.FileMetaRepositoryPort;
import com.jyjun.projectbp.application.file.outbound.FileStoragePort;
import com.jyjun.projectbp.domain.filemeta.FileMeta;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class UploadFileUseCase {

    private final FileStoragePort fileStoragePort;
    private final FileMetaRepositoryPort fileMetaRepositoryPort;

    public UploadFileUseCase(FileStoragePort fileStoragePort, FileMetaRepositoryPort fileMetaRepositoryPort) {
        this.fileStoragePort = fileStoragePort;
        this.fileMetaRepositoryPort = fileMetaRepositoryPort;
    }

    @Transactional
    public FileMeta execute(String originalName, long sizeByte, InputStream fileData) {

        // 파라미터를 바탕으로 파일 메타 정보 생성
        FileMeta fileMeta = new FileMeta(originalName, sizeByte);

        // 실제로 스토리지에 파일 저장
        // 이 계층에서는 어디에 저장되는지 모름. 로컬에 저장되거나 클라우드에 저장되거나 할 수 있음.
        fileStoragePort.save(fileMeta.getStoredName(), fileData);

        // 메타 정보 저장 및 반환
        return fileMetaRepositoryPort.save(fileMeta);
    }
}
