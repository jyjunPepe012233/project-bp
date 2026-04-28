package com.jyjun.projectbp.presentation.file;

import com.jyjun.projectbp.application.file.usecase.DownloadFileUseCase;
import com.jyjun.projectbp.application.file.usecase.UploadFileUseCase;
import com.jyjun.projectbp.application.model.output.DownloadFileOutput;
import com.jyjun.projectbp.domain.filemeta.model.FileMeta;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
public class FileController {

    private final DownloadFileUseCase downloadFileUseCase;
    private final UploadFileUseCase uploadFileUseCase;

    public FileController(DownloadFileUseCase downloadFileUseCase, UploadFileUseCase uploadFileUseCase) {
        this.downloadFileUseCase = downloadFileUseCase;
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FileMeta upload(HttpServletRequest request) throws Exception {

        // form-data에 file이라는 이름으로 파일을 담아 보내면
        // fileName은 업로드한 파일의 이름이 됨

        // Apache Commons FileUpload 기반 파일 업로드를 위해
        // JakartaServletFileUpload(자카르타 서블릿 전용 파일 파서) 인스턴스 생성
        JakartaServletFileUpload upload = new JakartaServletFileUpload();

        FileItemInputIterator iterator = upload.getItemIterator(request);

        // 각 바운더리를 파싱하면서 파일이 포함된 부분을 찾아 업로드 처리
        while (iterator.hasNext()) {
            FileItemInput item = iterator.next();
            // 바운더리 내의 각 필드가 파일인지 확인
            // 텍스트 필드가 아니라면(=!isFromField()) 파일로 간주하여 스트림을 업로드 유스케이스에 전달
            if (!item.isFormField()) {
                return uploadFileUseCase.execute(item.getName(), item.getInputStream());
            }
        }

        throw new IllegalArgumentException("요청에 파일 파트가 없습니다.");
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
