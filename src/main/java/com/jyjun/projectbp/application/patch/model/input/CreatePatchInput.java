package com.jyjun.projectbp.application.patch.model.input;

import com.jyjun.projectbp.common.validation.ValidationConstants;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreatePatchInput(
        Long gameId,

        @NotBlank(message = "버전을 입력해 주세요.")
        @Pattern(regexp = ValidationConstants.VERSION_PATTERN, message = ValidationConstants.VERSION_MESSAGE)
        String version,

        @NotNull(message = "플랫폼을 선택해 주세요.")
        PatchPlatform platform,

        @NotBlank(message = "패치 노트를 입력해 주세요.")
        @Size(max = ValidationConstants.PATCH_NOTE_MAX_LENGTH, message = "패치 노트는 " + ValidationConstants.PATCH_NOTE_MAX_LENGTH + "자 이하여야 합니다.")
        String patchNote
) {
}
