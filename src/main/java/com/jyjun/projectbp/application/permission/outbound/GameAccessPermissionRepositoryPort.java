package com.jyjun.projectbp.application.permission.outbound;

import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.model.GameAccessPermission;

public interface GameAccessPermissionRepositoryPort {

    GameAccessPermission save(GameAccessPermission gameAccessPermission);

    boolean existsByAccountIdAndGameIdAndPermission(Long accountId, Long gameId, GameAccessPermissionType permission);
}
