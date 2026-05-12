package com.jyjun.projectbp.presentation.patch;

import com.jyjun.projectbp.application.patch.model.input.CreatePatchInput;
import com.jyjun.projectbp.application.patch.model.input.UpdatePatchNoteInput;
import com.jyjun.projectbp.application.patch.model.input.UploadBundleInput;
import com.jyjun.projectbp.application.patch.model.input.UploadCatalogInput;
import com.jyjun.projectbp.application.patch.model.output.CreatePatchOutput;
import com.jyjun.projectbp.application.patch.model.output.LoadPatchOutput;
import com.jyjun.projectbp.application.patch.model.output.UpdatePatchNoteOutput;
import com.jyjun.projectbp.application.patch.model.output.UploadCatalogOutput;
import com.jyjun.projectbp.application.patch.usecase.CreatePatchUseCase;
import com.jyjun.projectbp.application.patch.usecase.LoadPatchUseCase;
import com.jyjun.projectbp.application.patch.usecase.UpdatePatchNoteUseCase;
import com.jyjun.projectbp.application.patch.usecase.UploadBundleUseCase;
import com.jyjun.projectbp.application.patch.usecase.UploadCatalogUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/games/{gameId}/patches")
public class PatchController {

    private final CreatePatchUseCase createPatchUseCase;
    private final LoadPatchUseCase loadPatchUseCase;
    private final UpdatePatchNoteUseCase updatePatchNoteUseCase;
    private final UploadCatalogUseCase uploadCatalogUseCase;
    private final UploadBundleUseCase uploadBundleUseCase;

    public PatchController(
            CreatePatchUseCase createPatchUseCase,
            LoadPatchUseCase loadPatchUseCase,
            UpdatePatchNoteUseCase updatePatchNoteUseCase,
            UploadCatalogUseCase uploadCatalogUseCase,
            UploadBundleUseCase uploadBundleUseCase
    ) {
        this.createPatchUseCase = createPatchUseCase;
        this.loadPatchUseCase = loadPatchUseCase;
        this.updatePatchNoteUseCase = updatePatchNoteUseCase;
        this.uploadCatalogUseCase = uploadCatalogUseCase;
        this.uploadBundleUseCase = uploadBundleUseCase;
    }

    @PostMapping
    public ResponseData<CreatePatchOutput> createPatch(@PathVariable Long gameId, @RequestBody CreatePatchInput input) {
        return new ResponseData<>(createPatchUseCase.execute(new CreatePatchInput(gameId, input.version(), input.platform(), input.patchNote())));
    }

    @GetMapping("/{patchId}")
    public ResponseData<LoadPatchOutput> loadPatch(@PathVariable Long gameId, @PathVariable Long patchId) {
        return new ResponseData<>(loadPatchUseCase.execute(gameId, patchId));
    }

    @PatchMapping("/{patchId}")
    public ResponseData<UpdatePatchNoteOutput> updatePatchNote(
            @PathVariable Long patchId,
            @RequestBody UpdatePatchNoteInput input
    ) {
        return new ResponseData<>(updatePatchNoteUseCase.execute(new UpdatePatchNoteInput(patchId, input.patchNote())));
    }

    @PostMapping(value = "/{patchId}/catalog", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseData<UploadCatalogOutput> uploadCatalog(
            @PathVariable Long patchId,
            HttpServletRequest request
    ) throws Exception {
        JakartaServletFileUpload upload = new JakartaServletFileUpload();
        FileItemInputIterator iterator = upload.getItemIterator(request);

        String catalogFilename = null;
        InputStream catalogData = null;
        String catalogHashFilename = null;
        InputStream catalogHashData = null;

        while (iterator.hasNext()) {
            FileItemInput item = iterator.next();
            if (item.isFormField()) continue;

            if ("catalog".equals(item.getFieldName())) {
                catalogFilename = item.getName();
                catalogData = item.getInputStream();
            } else if ("catalogHash".equals(item.getFieldName())) {
                catalogHashFilename = item.getName();
                catalogHashData = item.getInputStream();
            }
        }

        if (catalogFilename == null || catalogHashFilename == null) {
            throw new IllegalArgumentException("catalog, catalogHash 파일이 모두 필요합니다.");
        }

        UploadCatalogInput input = new UploadCatalogInput(patchId, catalogFilename, catalogData, catalogHashFilename, catalogHashData);
        return new ResponseData<>(uploadCatalogUseCase.execute(input));
    }

    @PostMapping(value = "/{patchId}/bundles", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

        throw new IllegalArgumentException("요청에 파일 파트가 없습니다.");
    }
}
