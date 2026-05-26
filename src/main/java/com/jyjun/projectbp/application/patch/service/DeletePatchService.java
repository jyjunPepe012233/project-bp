package com.jyjun.projectbp.application.patch.service;

import com.jyjun.projectbp.application.catalog.service.DeleteCatalogService;
import com.jyjun.projectbp.application.patch.outbound.PatchRepositoryPort;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Component;

@Component
public class DeletePatchService {

    private final DeleteCatalogService deleteCatalogService;
    private final PatchRepositoryPort patchRepositoryPort;

    public DeletePatchService(DeleteCatalogService deleteCatalogService, PatchRepositoryPort patchRepositoryPort) {
        this.deleteCatalogService = deleteCatalogService;
        this.patchRepositoryPort = patchRepositoryPort;
    }

    public void delete(Patch patch, String gameUuid) {
        // Catalog와 Catalog hash는 패치가 삭제되면 무조건 같이 사라져야 하므로 Service 레벨에서 처리함
        deleteCatalogService.deleteCatalog(gameUuid, patch.getPlatform(), patch.getVersion());
        deleteCatalogService.deleteCatalogHash(gameUuid, patch.getPlatform(), patch.getVersion());
        patchRepositoryPort.deleteById(patch.getId());
    }
}
