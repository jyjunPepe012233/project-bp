package com.jyjun.projectbp.presentation.file;

import com.jyjun.projectbp.application.file.usecase.DownloadFileUseCase;
import com.jyjun.projectbp.application.file.usecase.UploadFileUseCase;
import com.jyjun.projectbp.application.model.output.DownloadFileOutput;
import com.jyjun.projectbp.domain.filemeta.FileMeta;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    private final DownloadFileUseCase downloadFileUseCase;
    private final UploadFileUseCase uploadFileUseCase;

    public FileController(DownloadFileUseCase downloadFileUseCase, UploadFileUseCase uploadFileUseCase) {
        this.downloadFileUseCase = downloadFileUseCase;
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @PostMapping
    public FileMeta upload(@RequestParam MultipartFile file) throws Exception {
        return uploadFileUseCase.execute(
                file.getOriginalFilename(),
                file.getSize(),
                file.getInputStream()
        );
    }

    @GetMapping("/{storedName}")
    public ResponseEntity<InputStreamSource> downloadFile(@PathVariable String storedName) {
        DownloadFileOutput output = downloadFileUseCase.execute(storedName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + storedName)
                .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                .body(new InputStreamResource(output.fileData()));
    }
}
