package com.jyjun.projectbp.infrastructure.permission;

import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.developeraccesspermission.model.DeveloperAccessPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface JpaDeveloperAccessPermissionRepository extends JpaRepository<DeveloperAccessPermission, Long> {

    List<DeveloperAccessPermission> findByAccountId(Long accountId);

    List<DeveloperAccessPermission> findByAccountIdAndDeveloperId(Long accountId, Long developerId);

    Optional<DeveloperAccessPermission> findByAccountIdAndDeveloperIdAndPermission(Long accountId, Long developerId, DeveloperAccessPermissionType permission);

    boolean existsByAccountIdAndDeveloperIdAndPermission(Long accountId, Long developerId, DeveloperAccessPermissionType permission);

    @Transactional
    void deleteByAccountIdAndDeveloperId(Long accountId, Long developerId);
}
