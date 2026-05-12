package com.jyjun.projectbp.application.patch.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.patch.service.DeleteCatalogService;
import com.jyjun.projectbp.application.patch.service.LoadPatchService;
import com.jyjun.projectbp.application.patch.service.UpdatePatchCatalogService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.application.permission.util.HasGameAccessPermissionUtil;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.patch.model.Patch;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteCatalogUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadPatchService loadPatchService;
    private final LoadGameService loadGameService;
    private final DeleteCatalogService deleteCatalogService;
    private final UpdatePatchCatalogService updatePatchCatalogService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;
    private final HasGameAccessPermissionUtil hasGameAccessPermissionUtil;

    public DeleteCatalogUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadPatchService loadPatchService,
            LoadGameService loadGameService,
            DeleteCatalogService deleteCatalogService,
            UpdatePatchCatalogService updatePatchCatalogService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadPatchService = loadPatchService;
        this.loadGameService = loadGameService;
        this.deleteCatalogService = deleteCatalogService;
        this.updatePatchCatalogService = updatePatchCatalogService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
        this.hasGameAccessPermissionUtil = new HasGameAccessPermissionUtil(loadGameAccessPermissionService);
    }

    @Transactional
    public void execute(Long patchId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Patch patch = loadPatchService.loadByIdOrThrow(patchId);
        Game game = loadGameService.loadByIdOrThrow(patch.getGameId());
        Long developerId = game.getDeveloperId();
        Long gameId = game.getId();

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.PUBLISHER)) {
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.ADMIN)) {
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.MAINTAIN)) {
        } else {
            throw new AccessDeniedException("카탈로그를 삭제할 권한이 없습니다.");
        }

        String catalogFileName = patch.getCatalogFileName();
        if (catalogFileName != null && !catalogFileName.isBlank()) {
            String gameUuid = game.getUuid().toString();
            deleteCatalogService.delete(gameUuid, patch.getVersion(), patch.getPlatform(), catalogFileName);
        }

        updatePatchCatalogService.updateCatalogFileName(patchId, null);
    }
}
