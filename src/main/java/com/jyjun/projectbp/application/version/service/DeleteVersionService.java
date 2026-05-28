package com.jyjun.projectbp.application.version.service;

import com.jyjun.projectbp.application.version.outbound.VersionRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class DeleteVersionService {

    private final VersionRepositoryPort versionRepositoryPort;

    public DeleteVersionService(VersionRepositoryPort versionRepositoryPort) {
        this.versionRepositoryPort = versionRepositoryPort;
    }

    public void deleteByGameId(Long gameId) {
        versionRepositoryPort.deleteByGameId(gameId);
    }
}
