package com.jyjun.projectbp.application.bundle.outbound;

import java.io.InputStream;
import java.util.List;

public interface BundleFileStoragePort {

    void saveBundle(String gameUuid, String platform, String filename, InputStream data);

    void deleteBundle(String gameUuid, String platform, String filename);

    List<String> listBundleFiles(String gameUuid, String platform);
}
