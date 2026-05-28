package com.jyjun.projectbp.infrastructure.permission;

import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.model.GameAccessPermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaGameAccessPermissionRepository extends JpaRepository<GameAccessPermission, Long> {

    List<GameAccessPermission> findByAccountId(Long accountId);

    List<GameAccessPermission> findByGameId(Long gameId);

    List<GameAccessPermission> findByAccountIdAndGameId(Long accountId, Long gameId);

    Optional<GameAccessPermission> findByAccountIdAndGameIdAndPermission(Long accountId, Long gameId, GameAccessPermissionType permission);

    boolean existsByAccountIdAndGameIdAndPermission(Long accountId, Long gameId, GameAccessPermissionType permission);

    void deleteByAccountIdAndGameId(Long accountId, Long gameId);

    void deleteByAccountId(Long accountId);

    void deleteByGameId(Long gameId);
}
