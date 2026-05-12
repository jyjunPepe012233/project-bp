package com.jyjun.projectbp.application.patch.service;

import com.jyjun.projectbp.application.patch.outbound.AddressableFileStoragePort;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadBundleFileListService {

    private final AddressableFileStoragePort addressableFileStoragePort;

    public LoadBundleFileListService(AddressableFileStoragePort addressableFileStoragePort) {
        this.addressableFileStoragePort = addressableFileStoragePort;
    }

    public List<String> load(String gameUuid, String version, PatchPlatform platform) {
        return addressableFileStoragePort.listBundleFiles(
                gameUuid,
                version,
                platform.getFormattedName()
        );
    }
}
