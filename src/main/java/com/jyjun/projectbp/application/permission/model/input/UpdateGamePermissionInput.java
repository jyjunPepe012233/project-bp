package com.jyjun.projectbp.application.permission.model.input;

import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;

import java.util.List;

public record UpdateGamePermissionInput(
        Long accountId,
        Long gameId,
        List<GameAccessPermissionType> permissions
) {
}
