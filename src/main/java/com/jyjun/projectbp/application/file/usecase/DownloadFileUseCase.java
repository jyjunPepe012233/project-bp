package com.jyjun.projectbp.application.file.usecase;

import com.jyjun.projectbp.application.file.model.output.DownloadFileOutput;
import com.jyjun.projectbp.application.file.service.LoadFileMetaService;
import com.jyjun.projectbp.application.file.service.LoadFileService;
import com.jyjun.projectbp.domain.filemeta.model.FileMeta;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class DownloadFileUseCase {

    private final LoadFileMetaService loadFileMetaService;
    private final LoadFileService loadFileService;

    public DownloadFileUseCase(LoadFileMetaService loadFileMetaService, LoadFileService loadFileService) {
        this.loadFileMetaService = loadFileMetaService;
        this.loadFileService = loadFileService;
    }

    @Transactional
    public DownloadFileOutput execute(String originalName) {
        FileMeta fileMeta = loadFileMetaService.loadByOriginalName(originalName);
        InputStream fileData = loadFileService.load(fileMeta.getStoredName());
        return new DownloadFileOutput(
                fileMeta.getOriginalName(),
                fileMeta.getStoredName(),
                fileMeta.getSizeByte(),
                fileData
        );
    }
}
