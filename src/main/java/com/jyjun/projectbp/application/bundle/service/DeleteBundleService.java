package com.jyjun.projectbp.application.bundle.service;

import com.jyjun.projectbp.application.bundle.outbound.BundleFileStoragePort;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Component;

@Component
public class DeleteBundleService {

    private final BundleFileStoragePort bundleFileStoragePort;

    public DeleteBundleService(BundleFileStoragePort bundleFileStoragePort) {
        this.bundleFileStoragePort = bundleFileStoragePort;
    }

    public void delete(String gameUuid, PatchPlatform platform, String filename) {
        bundleFileStoragePort.deleteBundle(
                gameUuid,
                platform.getFormattedName(),
                filename
        );
    }
}
