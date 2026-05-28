package com.jyjun.projectbp.application.catalog.service;

import com.jyjun.projectbp.application.catalog.outbound.CatalogFileStoragePort;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Component;

@Component
public class DeleteCatalogService {

    private final CatalogFileStoragePort catalogFileStoragePort;

    public DeleteCatalogService(CatalogFileStoragePort catalogFileStoragePort) {
        this.catalogFileStoragePort = catalogFileStoragePort;
    }

    public void deleteCatalog(String gameUuid, PatchPlatform platform, String version) {
        catalogFileStoragePort.deleteCatalog(gameUuid, platform.getFormattedName(), version);
    }

    public void deleteCatalogHash(String gameUuid, PatchPlatform platform, String version) {
        catalogFileStoragePort.deleteCatalogHash(gameUuid, platform.getFormattedName(), version);
    }
}
