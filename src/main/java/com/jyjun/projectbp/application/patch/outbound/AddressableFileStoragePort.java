package com.jyjun.projectbp.application.patch.outbound;

import java.io.InputStream;

public interface AddressableFileStoragePort {

    void save(String gameUuid, String version, String platform, String filename, InputStream data);

    void delete(String gameUuid, String version, String platform, String filename);
}
