package com.jyjun.projectbp.application.patch.outbound;

import java.io.InputStream;
import java.util.List;

public interface AddressableFileStoragePort {

    void saveCatalog(String gameUuid, String version, String platform, String filename, InputStream data);

    void deleteCatalog(String gameUuid, String version, String platform, String filename);

    void saveBundle(String gameUuid, String version, String platform, String filename, InputStream data);

    void deleteBundle(String gameUuid, String version, String platform, String filename);

    List<String> listBundleFiles(String gameUuid, String version, String platform);
}
