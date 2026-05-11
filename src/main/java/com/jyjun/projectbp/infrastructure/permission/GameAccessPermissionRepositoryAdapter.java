package com.jyjun.projectbp.infrastructure.permission;

import com.jyjun.projectbp.application.permission.outbound.GameAccessPermissionRepositoryPort;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.model.GameAccessPermission;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GameAccessPermissionRepositoryAdapter implements GameAccessPermissionRepositoryPort {

    private final JpaGameAccessPermissionRepository jpaGameAccessPermissionRepository;

    public GameAccessPermissionRepositoryAdapter(JpaGameAccessPermissionRepository jpaGameAccessPermissionRepository) {
        this.jpaGameAccessPermissionRepository = jpaGameAccessPermissionRepository;
    }

    @Override
    public GameAccessPermission save(GameAccessPermission gameAccessPermission) {
        return jpaGameAccessPermissionRepository.save(gameAccessPermission);
    }

    @Override
    public boolean existsByAccountIdAndGameIdAndPermission(Long accountId, Long gameId, GameAccessPermissionType permission) {
        return jpaGameAccessPermissionRepository.existsByAccountIdAndGameIdAndPermission(accountId, gameId, permission);
    }

    @Override
    public List<GameAccessPermission> findByAccountId(Long accountId) {
        return jpaGameAccessPermissionRepository.findByAccountId(accountId);
    }

    @Override
    public List<GameAccessPermission> findByAccountIdAndGameId(Long accountId, Long gameId) {
        return jpaGameAccessPermissionRepository.findByAccountIdAndGameId(accountId, gameId);
    }

    @Override
    public Optional<GameAccessPermission> findByAccountIdAndGameIdAndPermission(Long accountId, Long gameId, GameAccessPermissionType permission) {
        return jpaGameAccessPermissionRepository.findByAccountIdAndGameIdAndPermission(accountId, gameId, permission);
    }
}
