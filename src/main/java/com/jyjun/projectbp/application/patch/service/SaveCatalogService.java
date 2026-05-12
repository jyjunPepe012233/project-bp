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

    public void save(String gameUuid, String version, PatchPlatform platform, String filename, InputStream data) {
        addressableFileStoragePort.saveCatalog(
                gameUuid,
                version,
                platform.getFormattedName(),
                filename,
                data
        );
    }
}
