package com.jyjun.projectbp.application.permission.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.permission.model.output.PermissionsOutput;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoadMyPermissionsUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;
    private final LoadGameAccessPermissionService loadGameAccessPermissionService;

    public LoadMyPermissionsUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;
        this.loadGameAccessPermissionService = loadGameAccessPermissionService;
    }

    public PermissionsOutput execute() {
        Long accountId = loadCurrentAccountService.getCurrentAccountId();

        // 계정의 개발자 권한 정보를 가져옴
        List<DeveloperAccessPermissionType> developerPermissions = loadDeveloperAccessPermissionService.loadByAccountId(accountId)
                .stream()
                .map(permission -> permission.getPermission())
                .toList();

        // 계정의 게임 권한 정보를 가져옴
        List<GameAccessPermissionType> gamePermissions = loadGameAccessPermissionService.loadByAccountId(accountId)
                .stream()
                .map(permission -> permission.getPermission())
                .toList();

        // 응답
        return new PermissionsOutput(developerPermissions, gamePermissions);
    }
}
