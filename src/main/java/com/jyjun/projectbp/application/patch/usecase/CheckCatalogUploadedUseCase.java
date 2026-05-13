package com.jyjun.projectbp.application.patch.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.patch.model.output.CatalogUploadedOutput;
import com.jyjun.projectbp.application.patch.outbound.AddressableFileStoragePort;
import com.jyjun.projectbp.application.patch.service.LoadPatchService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.application.permission.util.HasGameAccessPermissionUtil;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Service;

@Service
public class CheckCatalogUploadedUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadGameService loadGameService;
    private final LoadPatchService loadPatchService;
    private final AddressableFileStoragePort addressableFileStoragePort;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;
    private final HasGameAccessPermissionUtil hasGameAccessPermissionUtil;

    public CheckCatalogUploadedUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadGameService loadGameService,
            LoadPatchService loadPatchService,
            AddressableFileStoragePort addressableFileStoragePort,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadGameService = loadGameService;
        this.loadPatchService = loadPatchService;
        this.addressableFileStoragePort = addressableFileStoragePort;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
        this.hasGameAccessPermissionUtil = new HasGameAccessPermissionUtil(loadGameAccessPermissionService);
    }

    public CatalogUploadedOutput checkCatalog(Long patchId) {
        Patch patch = loadAndAuthorize(patchId);
        Game game = loadGameService.loadByIdOrThrow(patch.getGameId());
        String gameUuid = game.getUuid().toString();
        String platform = patch.getPlatform().getFormattedName();

        boolean uploaded = addressableFileStoragePort.catalogExists(gameUuid, platform, patch.getVersion());
        return new CatalogUploadedOutput(uploaded);
    }

    public CatalogUploadedOutput checkCatalogHash(Long patchId) {
        Patch patch = loadAndAuthorize(patchId);
        Game game = loadGameService.loadByIdOrThrow(patch.getGameId());
        String gameUuid = game.getUuid().toString();
        String platform = patch.getPlatform().getFormattedName();

        boolean uploaded = addressableFileStoragePort.catalogHashExists(gameUuid, platform, patch.getVersion());
        return new CatalogUploadedOutput(uploaded);
    }

    private Patch loadAndAuthorize(Long patchId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Patch patch = loadPatchService.loadByIdOrThrow(patchId);
        Game game = loadGameService.loadByIdOrThrow(patch.getGameId());
        Long developerId = game.getDeveloperId();
        Long gameId = game.getId();

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.PUBLISHER)) {
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.ADMIN)) {
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.PRIMARY_WRITE)) {
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.MAINTAIN)) {
        } else {
            throw new AccessDeniedException("카탈로그 상태를 조회할 권한이 없습니다.");
        }

        return patch;
    }
}
