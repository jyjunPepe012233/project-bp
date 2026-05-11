package com.jyjun.projectbp.application.permission.model.output;

import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;

import java.util.List;

public record UpdateDeveloperPermissionOutput(
        Long accountId,
        Long developerId,
        List<DeveloperAccessPermissionType> permissions
) {
}
