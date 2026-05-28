package com.jyjun.projectbp.application.game.service;

import com.jyjun.projectbp.application.account.service.DeleteAccountService;
import com.jyjun.projectbp.application.game.outbound.GameRepositoryPort;
import com.jyjun.projectbp.application.patch.service.DeletePatchService;
import com.jyjun.projectbp.application.patch.service.LoadPatchService;
import com.jyjun.projectbp.application.permission.service.DeleteGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.gameaccesspermission.model.GameAccessPermission;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DeleteGameService {

    private final LoadPatchService loadPatchService;
    private final DeletePatchService deletePatchService;
    private final LoadGameAccessPermissionService loadGameAccessPermissionService;
    private final DeleteGameAccessPermissionService deleteGameAccessPermissionService;
    private final DeleteAccountService deleteAccountService;
    private final GameRepositoryPort gameRepositoryPort;

    public DeleteGameService(
            LoadPatchService loadPatchService,
            DeletePatchService deletePatchService,
            LoadGameAccessPermissionService loadGameAccessPermissionService,
            DeleteGameAccessPermissionService deleteGameAccessPermissionService,
            DeleteAccountService deleteAccountService,
            GameRepositoryPort gameRepositoryPort
    ) {
        this.loadPatchService = loadPatchService;
        this.deletePatchService = deletePatchService;
        this.loadGameAccessPermissionService = loadGameAccessPermissionService;
        this.deleteGameAccessPermissionService = deleteGameAccessPermissionService;
        this.deleteAccountService = deleteAccountService;
        this.gameRepositoryPort = gameRepositoryPort;
    }

    public void delete(Long gameId, String gameUuid) {
        List<Patch> patches = loadPatchService.loadByGameId(gameId);
        for (Patch patch : patches) {
            deletePatchService.delete(patch, gameUuid);
        }

        Set<Long> affectedAccountIds = collectAffectedAccountIds(gameId);
        deleteGameAccessPermissionService.deleteByGameId(gameId);
        deleteIndependentAccounts(affectedAccountIds);
        gameRepositoryPort.deleteById(gameId);
    }

    private Set<Long> collectAffectedAccountIds(Long gameId) {
        List<GameAccessPermission> permissions = loadGameAccessPermissionService.loadByGameId(gameId);
        Set<Long> accountIds = new HashSet<>();
        for (GameAccessPermission permission : permissions) {
            accountIds.add(permission.getAccountId());
        }
        return accountIds;
    }

    private void deleteIndependentAccounts(Set<Long> accountIds) {
        for (Long accountId : accountIds) {
            deleteAccountService.deleteIfIndependent(accountId);
        }
    }
}
