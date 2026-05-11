package com.jyjun.projectbp.application.account.model.input;

import com.jyjun.projectbp.application.account.model.entry.DeveloperAccessPermissionEntry;
import com.jyjun.projectbp.application.account.model.entry.GameAccessPermissionEntry;

import java.util.List;

public record CreateAccountInput(
        String name,
        String password,
        List<DeveloperAccessPermissionEntry> developerAccessPermissions,
        List<GameAccessPermissionEntry> gameAccessPermissions
) {
}
