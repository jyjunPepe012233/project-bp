package com.jyjun.projectbp.application.game.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.LoadDevelopersByPermissionUtil;
import com.jyjun.projectbp.application.developer.util.LoadRootDeveloperUtil;
import com.jyjun.projectbp.application.game.model.output.LoadGameOutput;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.game.util.LoadGamesByPermissionUtil;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.developer.model.Developer;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoadGameListUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadGameService loadGameService;
    private final LoadRootDeveloperUtil loadRootDeveloperUtil;
    private final LoadDevelopersByPermissionUtil loadDevelopersByPermissionUtil;
    private final LoadGamesByPermissionUtil loadGamesByPermissionUtil;

    public LoadGameListUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadDeveloperService loadDeveloperService,
            LoadGameService loadGameService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadGameService = loadGameService;
        this.loadRootDeveloperUtil = new LoadRootDeveloperUtil(loadDeveloperService);
        this.loadDevelopersByPermissionUtil = new LoadDevelopersByPermissionUtil(loadDeveloperService, loadDeveloperAccessPermissionService);
        this.loadGamesByPermissionUtil = new LoadGamesByPermissionUtil(loadGameService, loadGameAccessPermissionService);
    }

    public List<LoadGameOutput> execute() {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        Map<Long, Game> games = new LinkedHashMap<>();

        // 루트 developer의 games
        Developer rootDeveloper = loadRootDeveloperUtil.load(currentAccountId);
        if (rootDeveloper != null) {
            loadGameService.loadByDeveloperId(rootDeveloper.getId())
                    .forEach(g -> games.put(g.getId(), g));
        }

        // developer permission이 있는 developer의 games
        loadDevelopersByPermissionUtil.load(currentAccountId, DeveloperAccessPermissionType.ADMIN)
                .forEach(d -> loadGameService.loadByDeveloperId(d.getId())
                        .forEach(g -> games.putIfAbsent(g.getId(), g)));
        loadDevelopersByPermissionUtil.load(currentAccountId, DeveloperAccessPermissionType.PUBLISHER)
                .forEach(d -> loadGameService.loadByDeveloperId(d.getId())
                        .forEach(g -> games.putIfAbsent(g.getId(), g)));

        // game permission이 있는 games
        loadGamesByPermissionUtil.load(currentAccountId, GameAccessPermissionType.ADMIN)
                .forEach(g -> games.putIfAbsent(g.getId(), g));
        loadGamesByPermissionUtil.load(currentAccountId, GameAccessPermissionType.PRIMARY_WRITE)
                .forEach(g -> games.putIfAbsent(g.getId(), g));
        loadGamesByPermissionUtil.load(currentAccountId, GameAccessPermissionType.MAINTAIN)
                .forEach(g -> games.putIfAbsent(g.getId(), g));

        return games.values().stream()
                .map(g -> new LoadGameOutput(g.getId(), g.getUuid(), g.getTitle(), g.getDescription(), g.getDeveloperId()))
                .toList();
    }
}
