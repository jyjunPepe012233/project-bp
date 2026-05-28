package com.jyjun.projectbp.application.patch.model.input;

import com.jyjun.projectbp.common.validation.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePatchNoteInput(
        Long patchId,

        @NotBlank(message = "패치 노트를 입력해 주세요.")
        @Size(max = ValidationConstants.PATCH_NOTE_MAX_LENGTH, message = "패치 노트는 " + ValidationConstants.PATCH_NOTE_MAX_LENGTH + "자 이하여야 합니다.")
        String patchNote
) {
}
