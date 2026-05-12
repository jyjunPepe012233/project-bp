package com.jyjun.projectbp.application.account.model.output;

import com.jyjun.projectbp.application.account.model.entry.DeveloperAccessPermissionEntry;
import com.jyjun.projectbp.application.account.model.entry.GameAccessPermissionEntry;

import java.util.List;

public record LoadMyPermissionsOutput(
        Long rootDeveloperId,
        List<DeveloperAccessPermissionEntry> developerPermissions,
        List<GameAccessPermissionEntry> gamePermissions
) {
}
