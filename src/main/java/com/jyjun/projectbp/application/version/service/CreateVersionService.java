package com.jyjun.projectbp.application.version.service;

import com.jyjun.projectbp.application.version.outbound.VersionRepositoryPort;
import com.jyjun.projectbp.domain.version.model.Version;
import org.springframework.stereotype.Component;

@Component
public class CreateVersionService {

    private final VersionRepositoryPort versionRepositoryPort;

    public CreateVersionService(VersionRepositoryPort versionRepositoryPort) {
        this.versionRepositoryPort = versionRepositoryPort;
    }

    public Version create(Long gameId) {
        Version version = new Version(gameId, null);
        return versionRepositoryPort.save(version);
    }
}
