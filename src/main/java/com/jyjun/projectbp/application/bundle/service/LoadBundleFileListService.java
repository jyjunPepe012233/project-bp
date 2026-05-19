package com.jyjun.projectbp.application.bundle.service;

import com.jyjun.projectbp.application.bundle.outbound.BundleFileStoragePort;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadBundleFileListService {

    private final BundleFileStoragePort bundleFileStoragePort;

    public LoadBundleFileListService(BundleFileStoragePort bundleFileStoragePort) {
        this.bundleFileStoragePort = bundleFileStoragePort;
    }

    public List<String> load(String gameUuid, PatchPlatform platform) {
        return bundleFileStoragePort.listBundleFiles(
                gameUuid,
                platform.getFormattedName()
        );
    }
}
