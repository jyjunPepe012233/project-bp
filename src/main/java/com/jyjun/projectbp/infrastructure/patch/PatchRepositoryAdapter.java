package com.jyjun.projectbp.infrastructure.patch;

import com.jyjun.projectbp.application.patch.outbound.PatchRepositoryPort;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PatchRepositoryAdapter implements PatchRepositoryPort {

    private final JpaPatchRepository jpaPatchRepository;

    public PatchRepositoryAdapter(JpaPatchRepository jpaPatchRepository) {
        this.jpaPatchRepository = jpaPatchRepository;
    }

    @Override
    public Patch save(Patch patch) {
        return jpaPatchRepository.save(patch);
    }

    @Override
    public Optional<Patch> findById(Long id) {
        return jpaPatchRepository.findById(id);
    }
}
