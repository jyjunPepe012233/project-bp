package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.output.LoadAccountOutput;
import com.jyjun.projectbp.application.auth.service.LoadAccountService;
import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.LoadDevelopersByPermissionUtil;
import com.jyjun.projectbp.application.developer.util.LoadRootDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.game.util.LoadGamesByPermissionUtil;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.developer.model.Developer;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
public class LoadAccountListUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadAccountService loadAccountService;
    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;
    private final LoadGameAccessPermissionService loadGameAccessPermissionService;
    private final LoadRootDeveloperUtil loadRootDeveloperUtil;
    private final LoadDevelopersByPermissionUtil loadDevelopersByPermissionUtil;
    private final LoadGamesByPermissionUtil loadGamesByPermissionUtil;

    public LoadAccountListUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadAccountService loadAccountService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameService loadGameService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadAccountService = loadAccountService;
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;
        this.loadGameAccessPermissionService = loadGameAccessPermissionService;
        this.loadRootDeveloperUtil = new LoadRootDeveloperUtil(loadDeveloperService);
        this.loadDevelopersByPermissionUtil = new LoadDevelopersByPermissionUtil(loadDeveloperService, loadDeveloperAccessPermissionService);
        this.loadGamesByPermissionUtil = new LoadGamesByPermissionUtil(loadGameService, loadGameAccessPermissionService);
    }

    public List<LoadAccountOutput> execute() {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        Set<Long> accountIds = new LinkedHashSet<>();
        accountIds.add(currentAccountId);

        // 루트인 developer의 모든 계정
        Developer rootDeveloper = loadRootDeveloperUtil.load(currentAccountId);
        if (rootDeveloper != null) {
            loadDeveloperAccessPermissionService.loadByDeveloperId(rootDeveloper.getId())
                    .forEach(p -> accountIds.add(p.getAccountId()));
        }

        // 내가 ADMIN인 developer의 모든 계정
        loadDevelopersByPermissionUtil.load(currentAccountId, DeveloperAccessPermissionType.ADMIN)
                .forEach(d -> loadDeveloperAccessPermissionService.loadByDeveloperId(d.getId())
                        .forEach(p -> accountIds.add(p.getAccountId())));

        // 내가 ADMIN인 game의 모든 계정
        loadGamesByPermissionUtil.load(currentAccountId, GameAccessPermissionType.ADMIN)
                .forEach(g -> loadGameAccessPermissionService.loadByGameId(g.getId())
                        .forEach(p -> accountIds.add(p.getAccountId())));

        return loadAccountService.loadAllByIds(new ArrayList<>(accountIds)).stream()
                .map(a -> new LoadAccountOutput(a.getId(), a.getName()))
                .toList();
    }
}
