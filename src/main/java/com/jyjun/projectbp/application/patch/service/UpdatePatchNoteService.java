package com.jyjun.projectbp.application.patch.service;

import com.jyjun.projectbp.application.patch.outbound.PatchRepositoryPort;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Component;

@Component
public class UpdatePatchNoteService {

    private final PatchRepositoryPort patchRepositoryPort;
    private final LoadPatchService loadPatchService;

    public UpdatePatchNoteService(PatchRepositoryPort patchRepositoryPort, LoadPatchService loadPatchService) {
        this.patchRepositoryPort = patchRepositoryPort;
        this.loadPatchService = loadPatchService;
    }

    public Patch update(Long patchId, String patchNote) {
        Patch patch = loadPatchService.loadByIdOrThrow(patchId);
        patch.setPatchNote(patchNote);
        return patchRepositoryPort.save(patch);
    }
}
