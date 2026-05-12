package com.jyjun.projectbp.application.game.util;

import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.game.model.Game;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;

import java.util.List;

public class LoadGamesByPermissionUtil {

    private final LoadGameService loadGameService;
    private final LoadGameAccessPermissionService loadGameAccessPermissionService;

    public LoadGamesByPermissionUtil(
            LoadGameService loadGameService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadGameService = loadGameService;
        this.loadGameAccessPermissionService = loadGameAccessPermissionService;
    }

    public List<Game> load(Long accountId, GameAccessPermissionType permission) {
        List<Long> gameIds = loadGameAccessPermissionService.loadByAccountId(accountId).stream()
                .filter(p -> p.getPermission() == permission)
                .map(p -> p.getGameId())
                .distinct()
                .toList();
        return loadGameService.loadAllByIds(gameIds);
    }
}
