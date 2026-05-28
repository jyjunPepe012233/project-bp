package com.jyjun.projectbp.presentation.permission;

import com.jyjun.projectbp.application.permission.model.input.UpdateDeveloperPermissionInput;
import com.jyjun.projectbp.application.permission.model.input.UpdateGamePermissionInput;
import com.jyjun.projectbp.application.permission.model.output.PermissionsOutput;
import com.jyjun.projectbp.application.permission.model.output.UpdateDeveloperPermissionOutput;
import com.jyjun.projectbp.application.permission.model.output.UpdateGamePermissionOutput;
import com.jyjun.projectbp.application.permission.usecase.LoadMyPermissionsUseCase;
import com.jyjun.projectbp.application.permission.usecase.LoadPermissionsUseCase;
import com.jyjun.projectbp.application.permission.usecase.UpdateDeveloperPermissionUseCase;
import com.jyjun.projectbp.application.permission.usecase.UpdateGamePermissionUseCase;
import com.jyjun.projectbp.common.dto.ResponseData;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PermissionController {

    private final LoadMyPermissionsUseCase loadMyPermissionsUseCase;
    private final LoadPermissionsUseCase loadPermissionsUseCase;
    private final UpdateDeveloperPermissionUseCase updateDeveloperPermissionUseCase;
    private final UpdateGamePermissionUseCase updateGamePermissionUseCase;

    public PermissionController(
            LoadMyPermissionsUseCase loadMyPermissionsUseCase,
            LoadPermissionsUseCase loadPermissionsUseCase,
            UpdateDeveloperPermissionUseCase updateDeveloperPermissionUseCase,
            UpdateGamePermissionUseCase updateGamePermissionUseCase
    ) {
        this.loadMyPermissionsUseCase = loadMyPermissionsUseCase;
        this.loadPermissionsUseCase = loadPermissionsUseCase;
        this.updateDeveloperPermissionUseCase = updateDeveloperPermissionUseCase;
        this.updateGamePermissionUseCase = updateGamePermissionUseCase;
    }

    @GetMapping("/accounts/me/permissions")
    public ResponseData<PermissionsOutput> loadMyPermissions() {
        return new ResponseData<>(loadMyPermissionsUseCase.execute());
    }

    @GetMapping("/accounts/{accountId}/permissions")
    public ResponseData<PermissionsOutput> loadAccountPermissions(@PathVariable Long accountId) {
        return new ResponseData<>(loadPermissionsUseCase.execute(accountId));
    }

    @PutMapping("/developers/{developerId}/permissions/{accountId}")
    public ResponseData<UpdateDeveloperPermissionOutput> updateDeveloperPermission(
            @PathVariable Long developerId,
            @PathVariable Long accountId,
            @RequestBody List<DeveloperAccessPermissionType> permissions
    ) {
        return new ResponseData<>(updateDeveloperPermissionUseCase.execute(
                new UpdateDeveloperPermissionInput(accountId, developerId, permissions)
        ));
    }

    @PutMapping("/games/{gameId}/permissions/{accountId}")
    public ResponseData<UpdateGamePermissionOutput> updateGamePermission(
            @PathVariable Long gameId,
            @PathVariable Long accountId,
            @RequestBody List<GameAccessPermissionType> permissions
    ) {
        return new ResponseData<>(updateGamePermissionUseCase.execute(
                new UpdateGamePermissionInput(accountId, gameId, permissions)
        ));
    }
}
