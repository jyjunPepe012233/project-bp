package com.jyjun.projectbp.infrastructure.permission;

import com.jyjun.projectbp.application.permission.outbound.DeveloperAccessPermissionRepositoryPort;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.developeraccesspermission.model.DeveloperAccessPermission;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
public class DeveloperAccessPermissionRepositoryAdapter implements DeveloperAccessPermissionRepositoryPort {

    private final JpaDeveloperAccessPermissionRepository jpaDeveloperAccessPermissionRepository;

    public DeveloperAccessPermissionRepositoryAdapter(JpaDeveloperAccessPermissionRepository jpaDeveloperAccessPermissionRepository) {
        this.jpaDeveloperAccessPermissionRepository = jpaDeveloperAccessPermissionRepository;
    }

    @Override
    public DeveloperAccessPermission save(DeveloperAccessPermission developerAccessPermission) {
        return jpaDeveloperAccessPermissionRepository.save(developerAccessPermission);
    }

    @Override
    public boolean existsByAccountIdAndDeveloperIdAndPermission(Long accountId, Long developerId, DeveloperAccessPermissionType permission) {
        return jpaDeveloperAccessPermissionRepository.existsByAccountIdAndDeveloperIdAndPermission(accountId, developerId, permission);
    }

    @Override
    public List<DeveloperAccessPermission> findByAccountId(Long accountId) {
        return jpaDeveloperAccessPermissionRepository.findByAccountId(accountId);
    }

    @Override
    public List<DeveloperAccessPermission> findByAccountIdAndDeveloperId(Long accountId, Long developerId) {
        return jpaDeveloperAccessPermissionRepository.findByAccountIdAndDeveloperId(accountId, developerId);
    }

    @Override
    public DeveloperAccessPermission findByAccountIdAndDeveloperIdAndPermission(Long accountId, Long developerId, DeveloperAccessPermissionType permission) {
        return jpaDeveloperAccessPermissionRepository.findByAccountIdAndDeveloperIdAndPermission(accountId, developerId, permission)
                .orElseThrow(() -> new NoSuchElementException("DeveloperAccessPermission not found"));
    }
}
