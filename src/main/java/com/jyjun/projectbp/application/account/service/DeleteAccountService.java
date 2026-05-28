package com.jyjun.projectbp.application.account.service;

import com.jyjun.projectbp.application.account.outbound.AccountRepositoryPort;
import com.jyjun.projectbp.application.auth.outbound.RefreshTokenRepositoryPort;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;

@Component
public class DeleteAccountService {

    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final LoadDeveloperService loadDeveloperService;
    private final LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService;
    private final LoadGameAccessPermissionService loadGameAccessPermissionService;

    public DeleteAccountService(
            RefreshTokenRepositoryPort refreshTokenRepositoryPort,
            AccountRepositoryPort accountRepositoryPort,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.refreshTokenRepositoryPort = refreshTokenRepositoryPort;
        this.accountRepositoryPort = accountRepositoryPort;
        this.loadDeveloperService = loadDeveloperService;
        this.loadDeveloperAccessPermissionService = loadDeveloperAccessPermissionService;
        this.loadGameAccessPermissionService = loadGameAccessPermissionService;
    }

    public void deleteIfIndependent(Long accountId) {
        if (isRootAccount(accountId)) {
            return;
        }

        boolean hasDeveloperPermissions = !loadDeveloperAccessPermissionService.loadByAccountId(accountId).isEmpty();
        boolean hasGamePermissions = !loadGameAccessPermissionService.loadByAccountId(accountId).isEmpty();

        if (!hasDeveloperPermissions && !hasGamePermissions) {
            deleteById(accountId);
        }
    }

    public void deleteById(Long accountId) {
        refreshTokenRepositoryPort.deleteAllByAccountId(accountId);
        accountRepositoryPort.deleteById(accountId);
    }

    private boolean isRootAccount(Long accountId) {
        try {
            loadDeveloperService.loadByRootAccountIdOrThrow(accountId);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
