package com.jyjun.projectbp.application.file.usecase;

import com.jyjun.projectbp.application.file.service.CreateFileMetaService;
import com.jyjun.projectbp.application.file.service.SaveFileService;
import com.jyjun.projectbp.domain.filemeta.model.FileMeta;
import com.jyjun.projectbp.domain.filemeta.service.GenerateFileStoreNameService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class UploadFileUseCase {

    private final GenerateFileStoreNameService generateFileStoreNameService;
    private final SaveFileService saveFileService;
    private final CreateFileMetaService createFileMetaService;

    public UploadFileUseCase(GenerateFileStoreNameService generateFileStoreNameService, SaveFileService saveFileService, CreateFileMetaService createFileMetaService) {
        this.generateFileStoreNameService = generateFileStoreNameService;
        this.saveFileService = saveFileService;
        this.createFileMetaService = createFileMetaService;
    }

    @Transactional
    public FileMeta execute(String originalName, InputStream fileData) {
        String storedName = generateFileStoreNameService.generate(originalName);
        long sizeByte = saveFileService.save(storedName, fileData);
        return createFileMetaService.create(originalName, storedName, sizeByte);
    }
}
