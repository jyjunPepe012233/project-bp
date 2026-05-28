package com.jyjun.projectbp.application.version.service;

import com.jyjun.projectbp.application.version.outbound.VersionRepositoryPort;
import com.jyjun.projectbp.domain.version.model.Version;
import org.springframework.stereotype.Component;

@Component
public class SaveVersionService {

    private final VersionRepositoryPort versionRepositoryPort;

    public SaveVersionService(VersionRepositoryPort versionRepositoryPort) {
        this.versionRepositoryPort = versionRepositoryPort;
    }

    public Version save(Long gameId, Long patchId) {
        Version version = versionRepositoryPort.findByGameId(gameId)
                .orElseGet(() -> new Version(gameId, patchId));

        version.setPatchId(patchId);
        return versionRepositoryPort.save(version);
    }
}
