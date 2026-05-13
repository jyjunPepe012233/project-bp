package com.jyjun.projectbp.application.patch.service;

import com.jyjun.projectbp.application.patch.outbound.AddressableFileStoragePort;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class SaveCatalogService {

    private final AddressableFileStoragePort addressableFileStoragePort;

    public SaveCatalogService(AddressableFileStoragePort addressableFileStoragePort) {
        this.addressableFileStoragePort = addressableFileStoragePort;
    }

    public void saveCatalog(String gameUuid, String version, PatchPlatform platform, InputStream data) {
        addressableFileStoragePort.saveCatalog(gameUuid, version, platform.getFormattedName(), data);
    }

    public void saveCatalogHash(String gameUuid, String version, PatchPlatform platform, InputStream data) {
        addressableFileStoragePort.saveCatalogHash(gameUuid, version, platform.getFormattedName(), data);
    }
}
