package com.jyjun.projectbp.application.patch.service;

import com.jyjun.projectbp.application.patch.outbound.PatchRepositoryPort;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Component;

@Component
public class UpdatePatchCatalogService {

    private final LoadPatchService loadPatchService;
    private final PatchRepositoryPort patchRepositoryPort;

    public UpdatePatchCatalogService(LoadPatchService loadPatchService, PatchRepositoryPort patchRepositoryPort) {
        this.loadPatchService = loadPatchService;
        this.patchRepositoryPort = patchRepositoryPort;
    }

    public Patch updateCatalogFileName(Long patchId, String catalogFileName) {
        Patch patch = loadPatchService.loadByIdOrThrow(patchId);
        patch.setCatalogFileName(catalogFileName);
        return patchRepositoryPort.save(patch);
    }

    public Patch updateCatalogHashFileName(Long patchId, String catalogHashFileName) {
        Patch patch = loadPatchService.loadByIdOrThrow(patchId);
        patch.setCatalogHashFileName(catalogHashFileName);
        return patchRepositoryPort.save(patch);
    }
}
