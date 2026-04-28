package com.jyjun.projectbp.application.file.outbound;

import com.jyjun.projectbp.domain.filemeta.model.FileMeta;

public interface FileMetaRepositoryPort {

    FileMeta save(FileMeta fileMeta);

    FileMeta findByStoredName(String storedName);
}
