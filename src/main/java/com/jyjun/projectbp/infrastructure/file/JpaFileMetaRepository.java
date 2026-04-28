package com.jyjun.projectbp.infrastructure.file;

import com.jyjun.projectbp.domain.filemeta.model.FileMeta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaFileMetaRepository extends JpaRepository<FileMeta, Long> {

    Optional<FileMeta> findByStoredName(String storedName);
}
