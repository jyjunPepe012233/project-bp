package com.jyjun.projectbp.application.patch.service;

import com.jyjun.projectbp.application.patch.outbound.PatchRepositoryPort;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class LoadPatchService {

    private final PatchRepositoryPort patchRepositoryPort;

    public LoadPatchService(PatchRepositoryPort patchRepositoryPort) {
        this.patchRepositoryPort = patchRepositoryPort;
    }

    public Patch loadByIdOrThrow(Long id) {
        return patchRepositoryPort.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Patch not found: id=" + id));
    }

    public List<Patch> loadByGameId(Long gameId) {
        return patchRepositoryPort.findByGameId(gameId);
    }
}
