package com.jyjun.projectbp.application.patch.outbound;

import java.io.InputStream;

public interface PatchFileStoragePort {

    void save(String gameUuid, String platform, String version, String filename, InputStream data);

    void delete(String gameUuid, String platform, String version, String filename);
}
