package com.jyjun.projectbp.presentation.catalog;

import com.jyjun.projectbp.application.catalog.model.input.UploadCatalogHashInput;
import com.jyjun.projectbp.application.catalog.model.input.UploadCatalogInput;
import com.jyjun.projectbp.application.catalog.model.output.CatalogUploadedOutput;
import com.jyjun.projectbp.application.catalog.model.output.UploadCatalogHashOutput;
import com.jyjun.projectbp.application.catalog.model.output.UploadCatalogOutput;
import com.jyjun.projectbp.application.catalog.usecase.CheckCatalogUploadedUseCase;
import com.jyjun.projectbp.application.catalog.usecase.DeleteCatalogHashUseCase;
import com.jyjun.projectbp.application.catalog.usecase.DeleteCatalogUseCase;
import com.jyjun.projectbp.application.catalog.usecase.UploadCatalogHashUseCase;
import com.jyjun.projectbp.application.catalog.usecase.UploadCatalogUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import com.jyjun.projectbp.common.exception.MissingFileException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class CatalogController {

    private final UploadCatalogUseCase uploadCatalogUseCase;
    private final UploadCatalogHashUseCase uploadCatalogHashUseCase;
    private final CheckCatalogUploadedUseCase checkCatalogUploadedUseCase;
    private final DeleteCatalogUseCase deleteCatalogUseCase;
    private final DeleteCatalogHashUseCase deleteCatalogHashUseCase;

    public CatalogController(
            UploadCatalogUseCase uploadCatalogUseCase,
            UploadCatalogHashUseCase uploadCatalogHashUseCase,
            CheckCatalogUploadedUseCase checkCatalogUploadedUseCase,
            DeleteCatalogUseCase deleteCatalogUseCase,
            DeleteCatalogHashUseCase deleteCatalogHashUseCase
    ) {
        this.uploadCatalogUseCase = uploadCatalogUseCase;
        this.uploadCatalogHashUseCase = uploadCatalogHashUseCase;
        this.checkCatalogUploadedUseCase = checkCatalogUploadedUseCase;
        this.deleteCatalogUseCase = deleteCatalogUseCase;
        this.deleteCatalogHashUseCase = deleteCatalogHashUseCase;
    }

    @PostMapping(value = "/patches/{patchId}/catalog", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<UploadCatalogOutput> uploadCatalog(
            @PathVariable Long patchId,
            HttpServletRequest request
    ) throws Exception {
        JakartaServletFileUpload upload = new JakartaServletFileUpload();
        FileItemInputIterator iterator = upload.getItemIterator(request);

        while (iterator.hasNext()) {
            FileItemInput item = iterator.next();
            if (!item.isFormField()) {
                UploadCatalogInput input = new UploadCatalogInput(patchId, item.getInputStream());
                return new ResponseData<>(uploadCatalogUseCase.execute(input));
            }
        }

        throw new MissingFileException("catalog 파일이 필요합니다.");
    }

    @PostMapping(value = "/patches/{patchId}/catalog-hash", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<UploadCatalogHashOutput> uploadCatalogHash(
            @PathVariable Long patchId,
            HttpServletRequest request
    ) throws Exception {
        JakartaServletFileUpload upload = new JakartaServletFileUpload();
        FileItemInputIterator iterator = upload.getItemIterator(request);

        while (iterator.hasNext()) {
            FileItemInput item = iterator.next();
            if (!item.isFormField()) {
                UploadCatalogHashInput input = new UploadCatalogHashInput(patchId, item.getInputStream());
                return new ResponseData<>(uploadCatalogHashUseCase.execute(input));
            }
        }

        throw new MissingFileException("catalogHash 파일이 필요합니다.");
    }

    @GetMapping("/patches/{patchId}/catalog/uploaded")
    public ResponseData<CatalogUploadedOutput> checkCatalogUploaded(@PathVariable Long patchId) {
        return new ResponseData<>(checkCatalogUploadedUseCase.checkCatalog(patchId));
    }

    @GetMapping("/patches/{patchId}/catalog-hash/uploaded")
    public ResponseData<CatalogUploadedOutput> checkCatalogHashUploaded(@PathVariable Long patchId) {
        return new ResponseData<>(checkCatalogUploadedUseCase.checkCatalogHash(patchId));
    }

    @DeleteMapping("/patches/{patchId}/catalog")
    public void deleteCatalog(@PathVariable Long patchId) {
        deleteCatalogUseCase.execute(patchId);
    }

    @DeleteMapping("/patches/{patchId}/catalog-hash")
    public void deleteCatalogHash(@PathVariable Long patchId) {
        deleteCatalogHashUseCase.execute(patchId);
    }
}
