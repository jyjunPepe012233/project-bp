package com.jyjun.projectbp.application.permission.service;

import com.jyjun.projectbp.application.permission.outbound.GameAccessPermissionRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class DeleteGameAccessPermissionService {

    private final GameAccessPermissionRepositoryPort gameAccessPermissionRepositoryPort;

    public DeleteGameAccessPermissionService(GameAccessPermissionRepositoryPort gameAccessPermissionRepositoryPort) {
        this.gameAccessPermissionRepositoryPort = gameAccessPermissionRepositoryPort;
    }

    public void deleteByAccountIdAndGameId(Long accountId, Long gameId) {
        gameAccessPermissionRepositoryPort.deleteByAccountIdAndGameId(accountId, gameId);
    }
}
