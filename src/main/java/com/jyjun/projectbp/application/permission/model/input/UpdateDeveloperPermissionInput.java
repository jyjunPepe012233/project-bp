package com.jyjun.projectbp.application.permission.model.input;

import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;

import java.util.List;

public record UpdateDeveloperPermissionInput(
        Long accountId,
        Long developerId,
        List<DeveloperAccessPermissionType> permissions
) {
}
