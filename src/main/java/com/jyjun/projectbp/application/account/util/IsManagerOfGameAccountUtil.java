package com.jyjun.projectbp.application.account.util;

import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.application.permission.util.HasGameAccessPermissionUtil;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;

public class IsManagerOfGameAccountUtil {

    private final LoadGameAccessPermissionService loadGameAccessPermissionService;
    private final LoadGameService loadGameService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;
    private final HasGameAccessPermissionUtil hasGameAccessPermissionUtil;

    public IsManagerOfGameAccountUtil(
            LoadGameAccessPermissionService loadGameAccessPermissionService,
            LoadGameService loadGameService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService
    ) {
        this.loadGameAccessPermissionService = loadGameAccessPermissionService;
        this.loadGameService = loadGameService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
        this.hasGameAccessPermissionUtil = new HasGameAccessPermissionUtil(loadGameAccessPermissionService);
    }

    public boolean is(Long currentAccountId, Long targetAccountId) {
        return loadGameAccessPermissionService.loadByAccountId(targetAccountId).stream()
                .anyMatch(perm -> {
                    Long developerId = loadGameService.loadByIdOrThrow(perm.getGameId()).getDeveloperId();
                    return isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)
                            || hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)
                            || hasGameAccessPermissionUtil.has(currentAccountId, perm.getGameId(), GameAccessPermissionType.ADMIN);
                });
    }
}
