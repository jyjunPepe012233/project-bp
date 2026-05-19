package com.jyjun.projectbp.application.catalog.service;

import com.jyjun.projectbp.application.catalog.outbound.CatalogFileStoragePort;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class SaveCatalogService {

    private final CatalogFileStoragePort catalogFileStoragePort;

    public SaveCatalogService(CatalogFileStoragePort catalogFileStoragePort) {
        this.catalogFileStoragePort = catalogFileStoragePort;
    }

    public void saveCatalog(String gameUuid, PatchPlatform platform, String version, InputStream data) {
        catalogFileStoragePort.saveCatalog(gameUuid, platform.getFormattedName(), version, data);
    }

    public void saveCatalogHash(String gameUuid, PatchPlatform platform, String version, InputStream data) {
        catalogFileStoragePort.saveCatalogHash(gameUuid, platform.getFormattedName(), version, data);
    }
}
