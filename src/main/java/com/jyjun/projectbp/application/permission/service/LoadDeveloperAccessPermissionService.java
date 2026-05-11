package com.jyjun.projectbp.application.permission.service;

import com.jyjun.projectbp.application.permission.outbound.DeveloperAccessPermissionRepositoryPort;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.developeraccesspermission.model.DeveloperAccessPermission;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;

@Component
public class LoadDeveloperAccessPermissionService {

    private final DeveloperAccessPermissionRepositoryPort developerAccessPermissionRepositoryPort;

    public LoadDeveloperAccessPermissionService(DeveloperAccessPermissionRepositoryPort developerAccessPermissionRepositoryPort) {
        this.developerAccessPermissionRepositoryPort = developerAccessPermissionRepositoryPort;
    }

    public List<DeveloperAccessPermission> loadByAccountId(Long accountId) {
        return developerAccessPermissionRepositoryPort.findByAccountId(accountId);
    }

    public List<DeveloperAccessPermission> loadByAccountIdAndDeveloperId(Long accountId, Long developerId) {
        return developerAccessPermissionRepositoryPort.findByAccountIdAndDeveloperId(accountId, developerId);
    }

    public DeveloperAccessPermission loadByAccountIdAndDeveloperIdAndPermissionOrThrow(Long accountId, Long developerId, DeveloperAccessPermissionType permission) {
        return developerAccessPermissionRepositoryPort.findByAccountIdAndDeveloperIdAndPermission(accountId, developerId, permission)
                .orElseThrow(() -> new NoSuchElementException("DeveloperAccessPermission not found"));
    }
}
