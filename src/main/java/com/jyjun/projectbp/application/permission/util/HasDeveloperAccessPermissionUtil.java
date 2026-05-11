package com.jyjun.projectbp.application.permission.util;

import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;

import java.util.NoSuchElementException;

public class HasDeveloperAccessPermissionUtil {

    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;

    public HasDeveloperAccessPermissionUtil(LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService) {
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;
    }

    public boolean has(Long accountId, Long developerId, DeveloperAccessPermissionType permission) {
        try {
            loadDeveloperAccessPermissionService.loadByAccountIdAndDeveloperIdAndPermissionOrThrow(accountId, developerId, permission);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
