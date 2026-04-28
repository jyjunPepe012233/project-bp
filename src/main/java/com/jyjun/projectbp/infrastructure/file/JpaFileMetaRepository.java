package com.jyjun.projectbp.infrastructure.file;

import com.jyjun.projectbp.domain.filemeta.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaFileMetaRepository extends JpaRepository<FileMeta, Long> {
}
