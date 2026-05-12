package com.jyjun.projectbp.application.permission.model.input;

import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateDeveloperPermissionInput(
        Long accountId,
        Long developerId,

        @NotNull(message = "권한 목록이 필요합니다.")
        List<DeveloperAccessPermissionType> permissions
) {
}
