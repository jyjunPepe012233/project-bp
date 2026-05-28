package com.jyjun.projectbp.application.version.service;

import com.jyjun.projectbp.application.patch.service.LoadPatchService;
import com.jyjun.projectbp.application.version.model.output.LoadVersionOutput;
import com.jyjun.projectbp.application.version.outbound.VersionRepositoryPort;
import com.jyjun.projectbp.common.exception.InvalidRequestException;
import com.jyjun.projectbp.domain.patch.model.Patch;
import com.jyjun.projectbp.domain.version.model.Version;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Component
public class LoadVersionService {

    private final VersionRepositoryPort versionRepositoryPort;
    private final LoadPatchService loadPatchService;

    public LoadVersionService(
            VersionRepositoryPort versionRepositoryPort,
            LoadPatchService loadPatchService
    ) {
        this.versionRepositoryPort = versionRepositoryPort;
        this.loadPatchService = loadPatchService;
    }

    public LoadVersionOutput loadResolvedByGameIdOrThrow(Long gameId) {
        Version version = versionRepositoryPort.findByGameId(gameId)
                .orElseThrow(() -> new NoSuchElementException("Version not found: gameId=" + gameId));

        Patch resolvedPatch = resolvePatch(gameId, version.getPatchId());
        return new LoadVersionOutput(
                version.getGameId(),
                version.getPatchId(),
                resolvedPatch == null ? null : resolvedPatch.getVersion()
        );
    }

    private Patch resolvePatch(Long gameId, Long patchId) {
        if (patchId != null) {
            Patch patch = loadPatchService.loadByIdOrThrow(patchId);
            if (!patch.getGameId().equals(gameId)) {
                throw new InvalidRequestException("Version의 patchId가 gameId와 일치하지 않습니다.");
            }
            return patch;
        }

        List<Patch> patches = loadPatchService.loadByGameId(gameId);
        return patches.stream()
                .max(Comparator
                        .comparing(Patch::getCreatedAt)
                        .thenComparing(Patch::getId))
                .orElse(null);
    }
}
