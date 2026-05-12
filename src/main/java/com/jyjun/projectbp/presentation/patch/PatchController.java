package com.jyjun.projectbp.presentation.patch;

import com.jyjun.projectbp.application.patch.model.input.CreatePatchInput;
import com.jyjun.projectbp.application.patch.model.output.CreatePatchOutput;
import com.jyjun.projectbp.application.patch.usecase.CreatePatchUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/games/{gameId}/patches")
public class PatchController {

    private final CreatePatchUseCase createPatchUseCase;

    public PatchController(CreatePatchUseCase createPatchUseCase) {
        this.createPatchUseCase = createPatchUseCase;
    }

    @PostMapping
    public ResponseData<CreatePatchOutput> createPatch(@PathVariable Long gameId) {
        return new ResponseData<>(createPatchUseCase.execute(new CreatePatchInput(gameId, input.version(), input.platform(), input.patchNote())));
    }
}
