package com.jyjun.projectbp.application.developer.service;

import com.jyjun.projectbp.application.account.service.DeleteAccountService;
import com.jyjun.projectbp.application.developer.outbound.DeveloperRepositoryPort;
import com.jyjun.projectbp.application.game.service.DeleteGameService;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.DeleteDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.domain.developeraccesspermission.model.DeveloperAccessPermission;
import com.jyjun.projectbp.domain.game.model.Game;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DeleteDeveloperService {

    private final LoadGameService loadGameService;
    private final DeleteGameService deleteGameService;
    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;
    private final DeleteDeveloperAccessPermissionService deleteDeveloperAccessPermissionService;
    private final DeleteAccountService deleteAccountService;
    private final DeveloperRepositoryPort developerRepositoryPort;

    public DeleteDeveloperService(
            LoadGameService loadGameService,
            DeleteGameService deleteGameService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            DeleteDeveloperAccessPermissionService deleteDeveloperAccessPermissionService,
            DeleteAccountService deleteAccountService,
            DeveloperRepositoryPort developerRepositoryPort
    ) {
        this.loadGameService = loadGameService;
        this.deleteGameService = deleteGameService;
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;
        this.deleteDeveloperAccessPermissionService = deleteDeveloperAccessPermissionService;
        this.deleteAccountService = deleteAccountService;
        this.developerRepositoryPort = developerRepositoryPort;
    }

    public void delete(Long developerId, Long rootAccountId) {
        List<Game> games = loadGameService.loadByDeveloperId(developerId);
        for (Game game : games) {
            deleteGameService.delete(game.getId(), game.getUuid().toString());
        }

        Set<Long> affectedAccountIds = collectAffectedAccountIds(developerId);
        deleteDeveloperAccessPermissionService.deleteByDeveloperId(developerId);

        for (Long accountId : affectedAccountIds) {
            deleteAccountService.deleteIfIndependent(accountId);
        }

        developerRepositoryPort.deleteById(developerId);
        deleteAccountService.deleteById(rootAccountId);
    }

    private Set<Long> collectAffectedAccountIds(Long developerId) {
        List<DeveloperAccessPermission> permissions =
                loadDeveloperAccessPermissionService.loadByDeveloperId(developerId);
        Set<Long> accountIds = new HashSet<>();
        for (DeveloperAccessPermission permission : permissions) {
            accountIds.add(permission.getAccountId());
        }
        return accountIds;
    }
}
