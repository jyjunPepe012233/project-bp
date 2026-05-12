package com.jyjun.projectbp.application.developer.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.model.output.LoadDeveloperOutput;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.LoadDevelopersByPermissionUtil;
import com.jyjun.projectbp.application.developer.util.LoadRootDeveloperUtil;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.domain.developer.model.Developer;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class LoadDeveloperListUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadRootDeveloperUtil loadRootDeveloperUtil;
    private final LoadDevelopersByPermissionUtil loadDevelopersByPermissionUtil;

    public LoadDeveloperListUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadRootDeveloperUtil = new LoadRootDeveloperUtil(loadDeveloperService);
        this.loadDevelopersByPermissionUtil = new LoadDevelopersByPermissionUtil(loadDeveloperService, loadDeveloperAccessPermissionService);
    }

    public List<LoadDeveloperOutput> execute() {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        Map<Long, Developer> developers = new LinkedHashMap<>();

        Developer rootDeveloper = loadRootDeveloperUtil.load(currentAccountId);
        if (rootDeveloper != null) {
            developers.put(rootDeveloper.getId(), rootDeveloper);
        }

        loadDevelopersByPermissionUtil.load(currentAccountId, DeveloperAccessPermissionType.ADMIN)
                .forEach(d -> developers.putIfAbsent(d.getId(), d));
        loadDevelopersByPermissionUtil.load(currentAccountId, DeveloperAccessPermissionType.PUBLISHER)
                .forEach(d -> developers.putIfAbsent(d.getId(), d));

        return developers.values().stream()
                .map(d -> new LoadDeveloperOutput(d.getId(), d.getName(), d.getRootAccountId()))
                .toList();
    }
}
