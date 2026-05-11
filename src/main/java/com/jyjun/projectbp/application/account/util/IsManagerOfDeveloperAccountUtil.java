package com.jyjun.projectbp.application.account.util;

import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;

public class IsManagerOfDeveloperAccountUtil {

    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;

    public IsManagerOfDeveloperAccountUtil(
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadDeveloperService loadDeveloperService
    ) {
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
    }

    public boolean is(Long currentAccountId, Long targetAccountId) {
        return loadDeveloperAccessPermissionService.loadByAccountId(targetAccountId).stream()
                .anyMatch(perm ->
                        isRootAccountOfDeveloperUtil.is(currentAccountId, perm.getDeveloperId())
                        || hasDeveloperAccessPermissionUtil.has(currentAccountId, perm.getDeveloperId(), DeveloperAccessPermissionType.ADMIN)
                );
    }
}
