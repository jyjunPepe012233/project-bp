package com.jyjun.projectbp.application.permission.model.output;

import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;

import java.util.List;

public record UpdateGamePermissionOutput(
        Long accountId,
        Long gameId,
        List<GameAccessPermissionType> permissions
) {
}
