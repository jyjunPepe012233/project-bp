package com.jyjun.projectbp.presentation.version;

import com.jyjun.projectbp.application.version.model.input.UpdateVersionInput;
import com.jyjun.projectbp.application.version.model.output.LoadVersionOutput;
import com.jyjun.projectbp.application.version.model.output.UpdateVersionOutput;
import com.jyjun.projectbp.application.version.usecase.LoadVersionUseCase;
import com.jyjun.projectbp.application.version.usecase.UpdateVersionUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class VersionController {

    private final LoadVersionUseCase loadVersionUseCase;
    private final UpdateVersionUseCase updateVersionUseCase;

    public VersionController(
            LoadVersionUseCase loadVersionUseCase,
            UpdateVersionUseCase updateVersionUseCase
    ) {
        this.loadVersionUseCase = loadVersionUseCase;
        this.updateVersionUseCase = updateVersionUseCase;
    }

    @GetMapping("/{gameId}/version")
    public ResponseData<LoadVersionOutput> loadVersion(@PathVariable Long gameId) {
        return new ResponseData<>(loadVersionUseCase.execute(gameId));
    }

    @PatchMapping("/{gameId}/version")
    public ResponseData<UpdateVersionOutput> updateVersion(
            @PathVariable Long gameId,
            @RequestBody UpdateVersionRequest request
    ) {
        return new ResponseData<>(updateVersionUseCase.execute(new UpdateVersionInput(gameId, request.patchId())));
    }

    public record UpdateVersionRequest(
            Long patchId
    ) {
    }
}
