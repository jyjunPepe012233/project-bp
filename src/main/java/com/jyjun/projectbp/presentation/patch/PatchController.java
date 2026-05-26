package com.jyjun.projectbp.presentation.patch;

import com.jyjun.projectbp.application.patch.model.input.CreatePatchInput;
import com.jyjun.projectbp.application.patch.model.input.UpdatePatchNoteInput;
import com.jyjun.projectbp.application.patch.model.output.CreatePatchOutput;
import com.jyjun.projectbp.application.patch.model.output.LoadPatchOutput;
import com.jyjun.projectbp.application.patch.model.output.UpdatePatchNoteOutput;
import com.jyjun.projectbp.application.patch.usecase.CreatePatchUseCase;
import com.jyjun.projectbp.application.patch.usecase.DeletePatchUseCase;
import com.jyjun.projectbp.application.patch.usecase.LoadPatchListUseCase;
import com.jyjun.projectbp.application.patch.usecase.LoadPatchUseCase;
import com.jyjun.projectbp.application.patch.usecase.UpdatePatchNoteUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PatchController {

    private final CreatePatchUseCase createPatchUseCase;
    private final LoadPatchListUseCase loadPatchListUseCase;
    private final LoadPatchUseCase loadPatchUseCase;
    private final UpdatePatchNoteUseCase updatePatchNoteUseCase;
    private final DeletePatchUseCase deletePatchUseCase;

    public PatchController(
            CreatePatchUseCase createPatchUseCase,
            LoadPatchListUseCase loadPatchListUseCase,
            LoadPatchUseCase loadPatchUseCase,
            UpdatePatchNoteUseCase updatePatchNoteUseCase,
            DeletePatchUseCase deletePatchUseCase
    ) {
        this.createPatchUseCase = createPatchUseCase;
        this.loadPatchListUseCase = loadPatchListUseCase;
        this.loadPatchUseCase = loadPatchUseCase;
        this.updatePatchNoteUseCase = updatePatchNoteUseCase;
        this.deletePatchUseCase = deletePatchUseCase;
    }

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

    @PatchMapping("/patches/{patchId}")
    public ResponseData<UpdatePatchNoteOutput> updatePatchNote(
            @PathVariable Long patchId,
            @Valid @RequestBody UpdatePatchNoteInput input
    ) {
        return new ResponseData<>(updatePatchNoteUseCase.execute(new UpdatePatchNoteInput(patchId, input.patchNote())));
    }

    @DeleteMapping("/patches/{patchId}")
    public void deletePatch(@PathVariable Long patchId) {
        deletePatchUseCase.execute(patchId);
    }
}
