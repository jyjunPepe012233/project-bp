package com.jyjun.projectbp.application.permission.outbound;

import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.developeraccesspermission.model.DeveloperAccessPermission;

import java.util.List;

public interface DeveloperAccessPermissionRepositoryPort {

    DeveloperAccessPermission save(DeveloperAccessPermission developerAccessPermission);

    boolean existsByAccountIdAndDeveloperIdAndPermission(Long accountId, Long developerId, DeveloperAccessPermissionType permission);

    List<DeveloperAccessPermission> findByAccountId(Long accountId);

    List<DeveloperAccessPermission> findByAccountIdAndDeveloperId(Long accountId, Long developerId);

    DeveloperAccessPermission findByAccountIdAndDeveloperIdAndPermission(Long accountId, Long developerId, DeveloperAccessPermissionType permission);
}
