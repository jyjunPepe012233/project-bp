package com.jyjun.projectbp.presentation.patch;

import com.jyjun.projectbp.application.patch.model.input.CreatePatchInput;
import com.jyjun.projectbp.application.patch.model.input.UpdatePatchNoteInput;
import com.jyjun.projectbp.application.patch.model.input.UploadBundleInput;
import com.jyjun.projectbp.application.patch.model.input.UploadCatalogHashInput;
import com.jyjun.projectbp.application.patch.model.input.UploadCatalogInput;
import com.jyjun.projectbp.application.patch.model.output.CreatePatchOutput;
import com.jyjun.projectbp.application.patch.model.output.LoadPatchOutput;
import com.jyjun.projectbp.application.patch.model.output.UpdatePatchNoteOutput;
import com.jyjun.projectbp.application.patch.model.output.UploadCatalogHashOutput;
import com.jyjun.projectbp.application.patch.model.output.LoadBundleFileListOutput;
import com.jyjun.projectbp.application.patch.model.output.LoadGameBundleListOutput;
import com.jyjun.projectbp.application.patch.model.output.UploadCatalogOutput;
import com.jyjun.projectbp.application.patch.model.output.CatalogUploadedOutput;
import com.jyjun.projectbp.application.patch.usecase.CheckCatalogUploadedUseCase;
import com.jyjun.projectbp.application.patch.usecase.CreatePatchUseCase;
import com.jyjun.projectbp.application.patch.usecase.DeleteCatalogHashUseCase;
import com.jyjun.projectbp.application.patch.usecase.DeleteCatalogUseCase;
import com.jyjun.projectbp.application.patch.usecase.LoadPatchListUseCase;
import com.jyjun.projectbp.application.patch.usecase.LoadPatchUseCase;
import com.jyjun.projectbp.application.patch.usecase.UpdatePatchNoteUseCase;
import com.jyjun.projectbp.application.patch.usecase.LoadBundleFileListUseCase;
import com.jyjun.projectbp.application.patch.usecase.LoadGameBundleListUseCase;
import com.jyjun.projectbp.application.patch.usecase.UploadBundleUseCase;
import com.jyjun.projectbp.application.patch.usecase.UploadCatalogHashUseCase;
import com.jyjun.projectbp.application.patch.usecase.UploadCatalogUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import com.jyjun.projectbp.common.exception.MissingFileException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PatchController {

    private final CreatePatchUseCase createPatchUseCase;
    private final LoadPatchListUseCase loadPatchListUseCase;
    private final LoadPatchUseCase loadPatchUseCase;
    private final UpdatePatchNoteUseCase updatePatchNoteUseCase;
    private final CheckCatalogUploadedUseCase checkCatalogUploadedUseCase;
    private final UploadCatalogUseCase uploadCatalogUseCase;
    private final UploadCatalogHashUseCase uploadCatalogHashUseCase;
    private final DeleteCatalogUseCase deleteCatalogUseCase;
    private final DeleteCatalogHashUseCase deleteCatalogHashUseCase;
    private final UploadBundleUseCase uploadBundleUseCase;
    private final LoadBundleFileListUseCase loadBundleFileListUseCase;
    private final LoadGameBundleListUseCase loadGameBundleListUseCase;

    public PatchController(
            CreatePatchUseCase createPatchUseCase,
            LoadPatchListUseCase loadPatchListUseCase,
            LoadPatchUseCase loadPatchUseCase,
            UpdatePatchNoteUseCase updatePatchNoteUseCase,
            CheckCatalogUploadedUseCase checkCatalogUploadedUseCase,
            UploadCatalogUseCase uploadCatalogUseCase,
            UploadCatalogHashUseCase uploadCatalogHashUseCase,
            DeleteCatalogUseCase deleteCatalogUseCase,
            DeleteCatalogHashUseCase deleteCatalogHashUseCase,
            UploadBundleUseCase uploadBundleUseCase,
            LoadBundleFileListUseCase loadBundleFileListUseCase,
            LoadGameBundleListUseCase loadGameBundleListUseCase
    ) {
        this.createPatchUseCase = createPatchUseCase;
        this.loadPatchListUseCase = loadPatchListUseCase;
        this.loadPatchUseCase = loadPatchUseCase;
        this.updatePatchNoteUseCase = updatePatchNoteUseCase;
        this.checkCatalogUploadedUseCase = checkCatalogUploadedUseCase;
        this.uploadCatalogUseCase = uploadCatalogUseCase;
        this.uploadCatalogHashUseCase = uploadCatalogHashUseCase;
        this.deleteCatalogUseCase = deleteCatalogUseCase;
        this.deleteCatalogHashUseCase = deleteCatalogHashUseCase;
        this.uploadBundleUseCase = uploadBundleUseCase;
        this.loadBundleFileListUseCase = loadBundleFileListUseCase;
        this.loadGameBundleListUseCase = loadGameBundleListUseCase;
    }



    // gameId가 필요한 엔드포인트는 /games/{gameId}/patches 로 시작

    @PostMapping("/games/{gameId}/patches")
    public ResponseData<CreatePatchOutput> createPatch(@PathVariable Long gameId, @Valid @RequestBody CreatePatchInput input) {
        return new ResponseData<>(createPatchUseCase.execute(new CreatePatchInput(gameId, input.version(), input.platform(), input.patchNote())));
    }

    @GetMapping("/games/{gameId}/patches/{patchId}")
    public ResponseData<LoadPatchOutput> loadPatch(@PathVariable Long gameId, @PathVariable Long patchId) {
        return new ResponseData<>(loadPatchUseCase.execute(gameId, patchId));
    }

    @GetMapping("/games/{gameId}/patches")
    public ResponseData<List<LoadPatchOutput>> loadPatchList(@PathVariable Long gameId) {
        return new ResponseData<>(loadPatchListUseCase.execute(gameId));
    }



    // --- gameId가 불필요한 엔드포인트는 /patches/{patchId}로 시작

    @PatchMapping("/patches/{patchId}")
    public ResponseData<UpdatePatchNoteOutput> updatePatchNote(
            @PathVariable Long patchId,
            @Valid @RequestBody UpdatePatchNoteInput input
    ) {
        return new ResponseData<>(updatePatchNoteUseCase.execute(new UpdatePatchNoteInput(patchId, input.patchNote())));
    }

    // Multipart 사용하면 Servlet이 메모리에 파일을 저장하는데, 이 때 OutOfMemory 에러가 발생하기 때문에
    // Apache Commons FileUpload 라이브러리를 사용하여 스트리밍 방식으로 파일을 처리하도록 구현함

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

    @PostMapping(value = "/patches/{patchId}/bundles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadBundle(
            @PathVariable Long patchId,
            HttpServletRequest request
    ) throws Exception {
        JakartaServletFileUpload upload = new JakartaServletFileUpload();
        FileItemInputIterator iterator = upload.getItemIterator(request);

        while (iterator.hasNext()) {
            FileItemInput item = iterator.next();
            if (!item.isFormField()) {
                uploadBundleUseCase.execute(new UploadBundleInput(patchId, item.getName(), item.getInputStream()));
                return;
            }
        }

        throw new MissingFileException("요청에 파일 파트가 없습니다.");
    }

    @GetMapping("/patches/{patchId}/catalog/uploaded")
    public ResponseData<CatalogUploadedOutput> checkCatalogUploaded(@PathVariable Long patchId) {
        return new ResponseData<>(checkCatalogUploadedUseCase.checkCatalog(patchId));
    }

    @GetMapping("/patches/{patchId}/catalog-hash/uploaded")
    public ResponseData<CatalogUploadedOutput> checkCatalogHashUploaded(@PathVariable Long patchId) {
        return new ResponseData<>(checkCatalogUploadedUseCase.checkCatalogHash(patchId));
    }

    @GetMapping("/patches/{patchId}/bundles")
    public ResponseData<LoadBundleFileListOutput> loadBundleFileList(@PathVariable Long patchId) {
        return new ResponseData<>(loadBundleFileListUseCase.execute(patchId));
    }

    @DeleteMapping("/patches/{patchId}/catalog")
    public void deleteCatalog(@PathVariable Long patchId) {
        deleteCatalogUseCase.execute(patchId);
    }

    @DeleteMapping("/patches/{patchId}/catalog-hash")
    public void deleteCatalogHash(@PathVariable Long patchId) {
        deleteCatalogHashUseCase.execute(patchId);
    }

    @GetMapping("/games/{gameId}/bundles")
    public ResponseData<LoadGameBundleListOutput> loadGameBundleList(@PathVariable Long gameId) {
        return new ResponseData<>(loadGameBundleListUseCase.execute(gameId));
    }
}
