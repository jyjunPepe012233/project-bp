package com.jyjun.projectbp.application.permission.service;

import com.jyjun.projectbp.application.permission.outbound.DeveloperAccessPermissionRepositoryPort;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.developeraccesspermission.model.DeveloperAccessPermission;
import org.springframework.stereotype.Component;

@Component
public class CreateDeveloperAccessPermissionService {

    private final DeveloperAccessPermissionRepositoryPort developerAccessPermissionRepositoryPort;

    public CreateDeveloperAccessPermissionService(DeveloperAccessPermissionRepositoryPort developerAccessPermissionRepositoryPort) {
        this.developerAccessPermissionRepositoryPort = developerAccessPermissionRepositoryPort;
    }

    public DeveloperAccessPermission create(Long accountId, Long developerId, DeveloperAccessPermissionType permission) {
        if (developerAccessPermissionRepositoryPort.existsByAccountIdAndDeveloperIdAndPermission(accountId, developerId, permission)) {
            throw new IllegalArgumentException("같은 설정의 개발자 접근 권한이 이미 존재합니다.");
        }
        DeveloperAccessPermission developerAccessPermission = new DeveloperAccessPermission(accountId, developerId, permission);
        return developerAccessPermissionRepositoryPort.save(developerAccessPermission);
    }
}
