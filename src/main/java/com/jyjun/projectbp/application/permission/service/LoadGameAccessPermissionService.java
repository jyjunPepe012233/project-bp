package com.jyjun.projectbp.application.permission.service;

import com.jyjun.projectbp.application.permission.outbound.GameAccessPermissionRepositoryPort;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.model.GameAccessPermission;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoadGameAccessPermissionService {

    private final GameAccessPermissionRepositoryPort gameAccessPermissionRepositoryPort;

    public LoadGameAccessPermissionService(GameAccessPermissionRepositoryPort gameAccessPermissionRepositoryPort) {
        this.gameAccessPermissionRepositoryPort = gameAccessPermissionRepositoryPort;
    }

    public List<GameAccessPermission> loadByAccountId(Long accountId) {
        return gameAccessPermissionRepositoryPort.findByAccountId(accountId);
    }

    public List<GameAccessPermission> loadByAccountIdAndGameId(Long accountId, Long gameId) {
        return gameAccessPermissionRepositoryPort.findByAccountIdAndGameId(accountId, gameId);
    }

    public GameAccessPermission loadByAccountIdAndGameIdAndPermission(Long accountId, Long gameId, GameAccessPermissionType permission) {
        return gameAccessPermissionRepositoryPort.findByAccountIdAndGameIdAndPermission(accountId, gameId, permission);
    }
}
