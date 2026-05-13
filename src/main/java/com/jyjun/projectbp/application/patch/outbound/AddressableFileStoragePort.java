package com.jyjun.projectbp.application.patch.outbound;

import java.io.InputStream;
import java.util.List;

public interface AddressableFileStoragePort {

    void saveCatalog(String gameUuid, String platform, String version, InputStream data);

    void deleteCatalog(String gameUuid, String platform, String version);

    boolean catalogExists(String gameUuid, String platform, String version);

    void saveCatalogHash(String gameUuid, String platform, String version, InputStream data);

    void deleteCatalogHash(String gameUuid, String platform, String version);

    boolean catalogHashExists(String gameUuid, String platform, String version);

    void saveBundle(String gameUuid, String platform, String version, String filename, InputStream data);

    void deleteBundle(String gameUuid, String platform, String version, String filename);

    List<String> listBundleFiles(String gameUuid, String platform, String version);
}
