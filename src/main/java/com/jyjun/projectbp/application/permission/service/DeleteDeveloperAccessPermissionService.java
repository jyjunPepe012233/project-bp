package com.jyjun.projectbp.application.permission.service;

import com.jyjun.projectbp.application.permission.outbound.DeveloperAccessPermissionRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class DeleteDeveloperAccessPermissionService {

    private final DeveloperAccessPermissionRepositoryPort developerAccessPermissionRepositoryPort;

    public DeleteDeveloperAccessPermissionService(DeveloperAccessPermissionRepositoryPort developerAccessPermissionRepositoryPort) {
        this.developerAccessPermissionRepositoryPort = developerAccessPermissionRepositoryPort;
    }

    public void deleteByAccountIdAndDeveloperId(Long accountId, Long developerId) {
        developerAccessPermissionRepositoryPort.deleteByAccountIdAndDeveloperId(accountId, developerId);
    }

    public void deleteByDeveloperId(Long developerId) {
        developerAccessPermissionRepositoryPort.deleteByDeveloperId(developerId);
    }
}
