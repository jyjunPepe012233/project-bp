package com.jyjun.projectbp.application.account.model.entry;

import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;

import java.util.List;

public record GameAccessPermissionEntry(
        Long gameId,
        List<GameAccessPermissionType> permissions
) {
}