package com.jyjun.projectbp.application.developer.util;

import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.domain.developer.model.Developer;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;

import java.util.List;

public class LoadDevelopersByPermissionUtil {

    private final LoadDeveloperService loadDeveloperService;
    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;

    public LoadDevelopersByPermissionUtil(
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService
    ) {
        this.loadDeveloperService = loadDeveloperService;
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;
    }

    public List<Developer> load(Long accountId, DeveloperAccessPermissionType permission) {
        List<Long> developerIds = loadDeveloperAccessPermissionService.loadByAccountId(accountId).stream()
                .filter(p -> p.getPermission() == permission)
                .map(p -> p.getDeveloperId())
                .distinct()
                .toList();
        return loadDeveloperService.loadAllByIds(developerIds);
    }
}
