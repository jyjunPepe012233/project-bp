package com.jyjun.projectbp.application.bundle.service;

import com.jyjun.projectbp.application.bundle.outbound.BundleFileStoragePort;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class SaveBundleService {

    private final BundleFileStoragePort bundleFileStoragePort;

    public SaveBundleService(BundleFileStoragePort bundleFileStoragePort) {
        this.bundleFileStoragePort = bundleFileStoragePort;
    }

    public void save(String gameUuid, PatchPlatform platform, String filename, InputStream data) {
        bundleFileStoragePort.saveBundle(
                gameUuid,
                platform.getFormattedName(),
                filename,
                data
        );
    }
}
