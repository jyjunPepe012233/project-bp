package com.jyjun.projectbp.application.permission.outbound;

import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.developeraccesspermission.model.DeveloperAccessPermission;

import java.util.List;
import java.util.Optional;

public interface DeveloperAccessPermissionRepositoryPort {

    DeveloperAccessPermission save(DeveloperAccessPermission developerAccessPermission);

    boolean existsByAccountIdAndDeveloperIdAndPermission(Long accountId, Long developerId, DeveloperAccessPermissionType permission);

    List<DeveloperAccessPermission> findByAccountId(Long accountId);

    List<DeveloperAccessPermission> findByDeveloperId(Long developerId);

    List<DeveloperAccessPermission> findByAccountIdAndDeveloperId(Long accountId, Long developerId);

    Optional<DeveloperAccessPermission> findByAccountIdAndDeveloperIdAndPermission(Long accountId, Long developerId, DeveloperAccessPermissionType permission);

    void deleteByAccountIdAndDeveloperId(Long accountId, Long developerId);

    void deleteByDeveloperId(Long developerId);
}
