package com.jyjun.projectbp.application.catalog.outbound;

import java.io.InputStream;

public interface CatalogFileStoragePort {

    void saveCatalog(String gameUuid, String platform, String version, InputStream data);

    void deleteCatalog(String gameUuid, String platform, String version);

    boolean catalogExists(String gameUuid, String platform, String version);

    void saveCatalogHash(String gameUuid, String platform, String version, InputStream data);

    void deleteCatalogHash(String gameUuid, String platform, String version);

    boolean catalogHashExists(String gameUuid, String platform, String version);
}
