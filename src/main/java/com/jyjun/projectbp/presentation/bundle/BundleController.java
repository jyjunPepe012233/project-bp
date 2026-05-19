package com.jyjun.projectbp.presentation.bundle;

import com.jyjun.projectbp.application.bundle.model.input.UploadBundleInput;
import com.jyjun.projectbp.application.bundle.model.output.LoadBundleFileListOutput;
import com.jyjun.projectbp.application.bundle.model.output.LoadGameBundleListOutput;
import com.jyjun.projectbp.application.bundle.usecase.LoadBundleFileListUseCase;
import com.jyjun.projectbp.application.bundle.usecase.LoadGameBundleListUseCase;
import com.jyjun.projectbp.application.bundle.usecase.UploadBundleUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import com.jyjun.projectbp.common.exception.MissingFileException;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class BundleController {

    private final UploadBundleUseCase uploadBundleUseCase;
    private final LoadBundleFileListUseCase loadBundleFileListUseCase;
    private final LoadGameBundleListUseCase loadGameBundleListUseCase;

    public BundleController(
            UploadBundleUseCase uploadBundleUseCase,
            LoadBundleFileListUseCase loadBundleFileListUseCase,
            LoadGameBundleListUseCase loadGameBundleListUseCase
    ) {
        this.uploadBundleUseCase = uploadBundleUseCase;
        this.loadBundleFileListUseCase = loadBundleFileListUseCase;
        this.loadGameBundleListUseCase = loadGameBundleListUseCase;
    }

    @PostMapping(value = "/games/{gameId}/bundles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadBundle(
            @PathVariable Long gameId,
            @RequestParam PatchPlatform platform,
            HttpServletRequest request
    ) throws Exception {
        JakartaServletFileUpload upload = new JakartaServletFileUpload();
        FileItemInputIterator iterator = upload.getItemIterator(request);

        while (iterator.hasNext()) {
            FileItemInput item = iterator.next();
            if (!item.isFormField()) {
                uploadBundleUseCase.execute(new UploadBundleInput(gameId, platform, item.getName(), item.getInputStream()));
                return;
            }
        }

        throw new MissingFileException("요청에 파일 파트가 없습니다.");
    }

    @GetMapping("/patches/{patchId}/bundles")
    public ResponseData<LoadBundleFileListOutput> loadBundleFileList(@PathVariable Long patchId) {
        return new ResponseData<>(loadBundleFileListUseCase.execute(patchId));
    }

    @GetMapping("/games/{gameId}/bundles")
    public ResponseData<LoadGameBundleListOutput> loadGameBundleList(@PathVariable Long gameId) {
        return new ResponseData<>(loadGameBundleListUseCase.execute(gameId));
    }
}
