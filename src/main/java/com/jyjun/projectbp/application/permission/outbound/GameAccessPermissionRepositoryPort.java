package com.jyjun.projectbp.application.permission.outbound;

import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.model.GameAccessPermission;

import java.util.List;
import java.util.Optional;

public interface GameAccessPermissionRepositoryPort {

    GameAccessPermission save(GameAccessPermission gameAccessPermission);

    boolean existsByAccountIdAndGameIdAndPermission(Long accountId, Long gameId, GameAccessPermissionType permission);

    List<GameAccessPermission> findByAccountId(Long accountId);

    List<GameAccessPermission> findByGameId(Long gameId);

    List<GameAccessPermission> findByAccountIdAndGameId(Long accountId, Long gameId);

    Optional<GameAccessPermission> findByAccountIdAndGameIdAndPermission(Long accountId, Long gameId, GameAccessPermissionType permission);

    void deleteByAccountIdAndGameId(Long accountId, Long gameId);

    void deleteByGameId(Long gameId);
}
