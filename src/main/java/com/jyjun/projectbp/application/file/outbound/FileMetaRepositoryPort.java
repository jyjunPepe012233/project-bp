package com.jyjun.projectbp.application.file.outbound;

import com.jyjun.projectbp.domain.filemeta.FileMeta;

public interface FileMetaRepositoryPort {

    FileMeta save(FileMeta fileMeta);
}
