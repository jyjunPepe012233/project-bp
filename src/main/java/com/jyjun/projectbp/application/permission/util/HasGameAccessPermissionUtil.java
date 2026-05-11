package com.jyjun.projectbp.application.permission.util;

import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;

import java.util.NoSuchElementException;

public class HasGameAccessPermissionUtil {

    private final LoadGameAccessPermissionService loadGameAccessPermissionService;

    public HasGameAccessPermissionUtil(LoadGameAccessPermissionService loadGameAccessPermissionService) {
        this.loadGameAccessPermissionService = loadGameAccessPermissionService;
    }

    public boolean has(Long accountId, Long gameId, GameAccessPermissionType permission) {
        try {
            loadGameAccessPermissionService.loadByAccountIdAndGameIdAndPermissionOrThrow(accountId, gameId, permission);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
