package com.jyjun.projectbp.application.developer.util;

import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.domain.developer.model.Developer;

import java.util.List;

public class GetDeveloperOfAccountUtil {

    private final LoadDeveloperService loadDeveloperService;
    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;

    public GetDeveloperOfAccountUtil(LoadDeveloperService loadDeveloperService, LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService) {
        this.loadDeveloperService = loadDeveloperService;
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;
    }

    public List<Long> getOnlyId(Long accountId) {
        return loadDeveloperAccessPermissionService.loadByAccountId(accountId).stream()
                .map(p -> p.getDeveloperId())
                .toList();
    }

    public List<Developer> get(Long accountId) {
        return loadDeveloperAccessPermissionService.loadByAccountId(accountId).stream()
                .map(p -> p.getDeveloperId())
                .distinct() // 중복된 개발자 ID 제거
                .map(developerId -> loadDeveloperService.loadByIdOrThrow(developerId))
                .toList();
    }
}
