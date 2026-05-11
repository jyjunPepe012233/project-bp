package com.jyjun.projectbp.application.permission.service;

import com.jyjun.projectbp.application.permission.outbound.GameAccessPermissionRepositoryPort;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.model.GameAccessPermission;
import org.springframework.stereotype.Component;

@Component
public class CreateGameAccessPermissionService {

    private final GameAccessPermissionRepositoryPort gameAccessPermissionRepositoryPort;

    public CreateGameAccessPermissionService(GameAccessPermissionRepositoryPort gameAccessPermissionRepositoryPort) {
        this.gameAccessPermissionRepositoryPort = gameAccessPermissionRepositoryPort;
    }

    public GameAccessPermission create(Long accountId, Long gameId, GameAccessPermissionType permission) {
        if (gameAccessPermissionRepositoryPort.existsByAccountIdAndGameIdAndPermission(accountId, gameId, permission)) {
            throw new IllegalArgumentException("같은 설정의 게임 접근 권한이 이미 존재합니다.");
        }
        GameAccessPermission gameAccessPermission = new GameAccessPermission(accountId, gameId, permission);
        return gameAccessPermissionRepositoryPort.save(gameAccessPermission);
    }
}
