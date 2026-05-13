package com.jyjun.projectbp.application.patch.service;

import com.jyjun.projectbp.application.patch.outbound.AddressableFileStoragePort;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class SaveBundleService {

    private final AddressableFileStoragePort addressableFileStoragePort;

    public SaveBundleService(AddressableFileStoragePort addressableFileStoragePort) {
        this.addressableFileStoragePort = addressableFileStoragePort;
    }

    public void save(String gameUuid, PatchPlatform platform, String version, String filename, InputStream data) {
        addressableFileStoragePort.saveBundle(
                gameUuid,
                platform.getFormattedName(),
                version,
                filename,
                data
        );
    }
}
