package com.jyjun.projectbp.application.permission.model.input;

import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateGamePermissionInput(
        Long accountId,
        Long gameId,

        @NotNull(message = "권한 목록이 필요합니다.")
        List<GameAccessPermissionType> permissions
) {
}
