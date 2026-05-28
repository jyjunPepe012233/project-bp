package com.jyjun.projectbp.application.permission.model.output;

import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;

import java.util.List;

public record PermissionsOutput(
        List<DeveloperAccessPermissionType> developerPermissions,
        List<GameAccessPermissionType> gamePermissions
) {
}
