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

    public void delete(String gameUuid, String version, PatchPlatform platform, String filename) {
        addressableFileStoragePort.deleteCatalog(
                gameUuid,
                version,
                platform.getFormattedName(),
                filename
        );
    }
}
