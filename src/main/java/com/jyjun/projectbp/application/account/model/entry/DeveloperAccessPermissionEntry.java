package com.jyjun.projectbp.application.account.model.entry;

import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;

import java.util.List;

public record DeveloperAccessPermissionEntry(
        Long developerId,
        List<DeveloperAccessPermissionType> permissions
) {
}
