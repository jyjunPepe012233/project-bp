package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.entry.DeveloperAccessPermissionEntry;
import com.jyjun.projectbp.application.account.model.entry.GameAccessPermissionEntry;
import com.jyjun.projectbp.application.account.model.output.LoadMyPermissionsOutput;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.LoadRootDeveloperUtil;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.developer.model.Developer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoadAccountPermissionsUseCase {

    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;
    private final LoadGameAccessPermissionService loadGameAccessPermissionService;
    private final LoadRootDeveloperUtil loadRootDeveloperUtil;

    public LoadAccountPermissionsUseCase(
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;
        this.loadGameAccessPermissionService = loadGameAccessPermissionService;
        this.loadRootDeveloperUtil = new LoadRootDeveloperUtil(loadDeveloperService);
    }

    public LoadMyPermissionsOutput execute(Long accountId) {
        Developer rootDeveloper = loadRootDeveloperUtil.load(accountId);
        Long rootDeveloperId = rootDeveloper != null ? rootDeveloper.getId() : null;

        List<DeveloperAccessPermissionEntry> developerPermissions =
                loadDeveloperAccessPermissionService.loadByAccountId(accountId).stream()
                        .collect(Collectors.groupingBy(
                                p -> p.getDeveloperId(),
                                Collectors.mapping(p -> p.getPermission(), Collectors.toList())
                        ))
                        .entrySet().stream()
                        .map(e -> new DeveloperAccessPermissionEntry(e.getKey(), e.getValue()))
                        .toList();

        List<GameAccessPermissionEntry> gamePermissions =
                loadGameAccessPermissionService.loadByAccountId(accountId).stream()
                        .collect(Collectors.groupingBy(
                                p -> p.getGameId(),
                                Collectors.mapping(p -> p.getPermission(), Collectors.toList())
                        ))
                        .entrySet().stream()
                        .map(e -> new GameAccessPermissionEntry(e.getKey(), e.getValue()))
                        .toList();

        return new LoadMyPermissionsOutput(rootDeveloperId, developerPermissions, gamePermissions);
    }
}
