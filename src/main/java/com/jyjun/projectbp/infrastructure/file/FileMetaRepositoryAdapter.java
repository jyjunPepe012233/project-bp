package com.jyjun.projectbp.infrastructure.file;

import com.jyjun.projectbp.application.file.outbound.FileMetaRepositoryPort;
import com.jyjun.projectbp.domain.filemeta.model.FileMeta;
import org.springframework.stereotype.Repository;

@Repository
public class FileMetaRepositoryAdapter implements FileMetaRepositoryPort {

    private final JpaFileMetaRepository jpaFileMetaRepository;

    public FileMetaRepositoryAdapter(JpaFileMetaRepository jpaFileMetaRepository) {
        this.jpaFileMetaRepository = jpaFileMetaRepository;
     }

     public FileMeta save(FileMeta fileMeta) {
         return jpaFileMetaRepository.save(fileMeta);
     }

     public FileMeta findByStoredName(String storedName) {
         return jpaFileMetaRepository.findByStoredName(storedName)
                 .orElseThrow(() -> new RuntimeException("FileMeta not found for storedName: " + storedName));
     }
}
