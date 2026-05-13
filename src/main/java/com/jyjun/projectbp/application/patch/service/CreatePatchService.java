package com.jyjun.projectbp.application.patch.service;

import com.jyjun.projectbp.application.patch.outbound.PatchRepositoryPort;
import com.jyjun.projectbp.common.exception.DuplicateResourceException;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Component;

@Component
public class CreatePatchService {

    private final PatchRepositoryPort patchRepositoryPort;

    public CreatePatchService(PatchRepositoryPort patchRepositoryPort) {
        this.patchRepositoryPort = patchRepositoryPort;
    }

    public Patch create(Long gameId, String version, PatchPlatform platform, String patchNote) {
        if (patchRepositoryPort.existsByGameIdAndVersionAndPlatform(gameId, version, platform)) {
            throw new DuplicateResourceException("같은 버전과 플랫폼의 패치가 이미 존재합니다.");
        }
        Patch patch = new Patch(gameId, version, platform, patchNote, null);
        return patchRepositoryPort.save(patch);
    }
}
