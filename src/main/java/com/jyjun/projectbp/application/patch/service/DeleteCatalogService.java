package com.jyjun.projectbp.application.patch.service;

import com.jyjun.projectbp.application.patch.outbound.AddressableFileStoragePort;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Component;

@Component
public class DeleteCatalogService {

    private final AddressableFileStoragePort addressableFileStoragePort;

    public DeleteCatalogService(AddressableFileStoragePort addressableFileStoragePort) {
        this.addressableFileStoragePort = addressableFileStoragePort;
    }

    public void deleteCatalog(String gameUuid, PatchPlatform platform, String version) {
        addressableFileStoragePort.deleteCatalog(gameUuid, platform.getFormattedName(), version);
    }

    public void deleteCatalogHash(String gameUuid, PatchPlatform platform, String version) {
        addressableFileStoragePort.deleteCatalogHash(gameUuid, platform.getFormattedName(), version);
    }
}
