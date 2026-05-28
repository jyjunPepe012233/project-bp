package com.jyjun.projectbp.application.permission.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.LoadRootDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.model.output.PermissionsOutput;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.developer.model.Developer;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class LoadPermissionsUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;
    private final LoadGameAccessPermissionService loadGameAccessPermissionService;
    private final LoadGameService loadGameService;
    private final LoadRootDeveloperUtil loadRootDeveloperUtil;

    public LoadPermissionsUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameService loadGameService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;
        this.loadGameAccessPermissionService = loadGameAccessPermissionService;
        this.loadGameService = loadGameService;
        this.loadRootDeveloperUtil = new LoadRootDeveloperUtil(loadDeveloperService);
    }

    public PermissionsOutput execute(Long accountId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        Set<DeveloperAccessPermissionType> developerPermissions = new LinkedHashSet<>();
        Set<GameAccessPermissionType> gamePermissions = new LinkedHashSet<>();

        // 1) accountId의 모든 Developer Permission을 불러옴
        var devPerms = loadDeveloperAccessPermissionService.loadByAccountId(accountId);
        Set<Long> developerIds = new LinkedHashSet<>();
        devPerms.forEach(p -> developerIds.add(p.getDeveloperId()));

        // 2) 1번의 권한 중 내가(currentAccount) 루트이거나 ADMIN 권한이 있는 Developer와 연결된 모든 Developer Permission 및 해당 Developer의 모든 Game에 대한 Game Permission을 응답에 포함시킴
        Set<Long> adminDeveloperIds = new LinkedHashSet<>();

        Developer rootDeveloper = loadRootDeveloperUtil.load(currentAccountId);
        if (rootDeveloper != null && developerIds.contains(rootDeveloper.getId())) {
            adminDeveloperIds.add(rootDeveloper.getId());
        }

        loadDeveloperAccessPermissionService.loadByAccountId(currentAccountId).stream()
                .filter(p -> p.getPermission() == DeveloperAccessPermissionType.ADMIN)
                .filter(p -> developerIds.contains(p.getDeveloperId()))
                .forEach(p -> adminDeveloperIds.add(p.getDeveloperId()));

        adminDeveloperIds.forEach(developerId -> {
            loadDeveloperAccessPermissionService.loadByDeveloperId(developerId)
                    .forEach(p -> developerPermissions.add(p.getPermission()));
            loadGameService.loadByDeveloperId(developerId)
                    .forEach(g -> loadGameAccessPermissionService.loadByGameId(g.getId())
                            .forEach(p -> gamePermissions.add(p.getPermission())));
        });

        // 3) accountId의 모든 Game Permission을 불러옴
        var gamePerms = loadGameAccessPermissionService.loadByAccountId(accountId);
        Set<Long> gameIds = new LinkedHashSet<>();
        gamePerms.forEach(p -> gameIds.add(p.getGameId()));

        // 4) 3번의 권한 중 내가(currentAccount) ADMIN 권한이 있는 Game과 연결된 모든 Game Permission을 응답에 포함시킴
        loadGameAccessPermissionService.loadByAccountId(currentAccountId).stream()
                .filter(p -> p.getPermission() == GameAccessPermissionType.ADMIN)
                .filter(p -> gameIds.contains(p.getGameId()))
                .forEach(p -> loadGameAccessPermissionService.loadByGameId(p.getGameId())
                        .forEach(gp -> gamePermissions.add(gp.getPermission())));

        return new PermissionsOutput(new ArrayList<>(developerPermissions), new ArrayList<>(gamePermissions));
    }
}
