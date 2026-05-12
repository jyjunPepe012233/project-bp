package com.jyjun.projectbp.application.patch.model.input;

public record UpdatePatchNoteInput(
        Long patchId,
        String patchNote
) {
}
