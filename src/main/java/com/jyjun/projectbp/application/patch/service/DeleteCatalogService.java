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

    public void deleteCatalog(String gameUuid, String version, PatchPlatform platform) {
        addressableFileStoragePort.deleteCatalog(gameUuid, version, platform.getFormattedName());
    }

    public void deleteCatalogHash(String gameUuid, String version, PatchPlatform platform) {
        addressableFileStoragePort.deleteCatalogHash(gameUuid, version, platform.getFormattedName());
    }
}
