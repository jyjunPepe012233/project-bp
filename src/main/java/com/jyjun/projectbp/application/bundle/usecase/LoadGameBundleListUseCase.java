package com.jyjun.projectbp.application.bundle.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.bundle.model.output.LoadGameBundleListOutput;
import com.jyjun.projectbp.application.bundle.model.output.LoadGameBundleListOutput.PlatformBundleEntry;
import com.jyjun.projectbp.application.bundle.outbound.BundleFileStoragePort;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.application.permission.util.HasGameAccessPermissionUtil;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.patch.enums.PatchPlatform;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class LoadGameBundleListUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadGameService loadGameService;
    private final BundleFileStoragePort bundleFileStoragePort;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;
    private final HasGameAccessPermissionUtil hasGameAccessPermissionUtil;

    public LoadGameBundleListUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadGameService loadGameService,
            BundleFileStoragePort bundleFileStoragePort,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadGameService = loadGameService;
        this.bundleFileStoragePort = bundleFileStoragePort;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
        this.hasGameAccessPermissionUtil = new HasGameAccessPermissionUtil(loadGameAccessPermissionService);
    }

    public LoadGameBundleListOutput execute(Long gameId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Game game = loadGameService.loadByIdOrThrow(gameId);
        Long developerId = game.getDeveloperId();

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.PUBLISHER)) {
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.ADMIN)) {
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.PRIMARY_WRITE)) {
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.MAINTAIN)) {
        } else {
            throw new AccessDeniedException("번들 목록을 조회할 권한이 없습니다.");
        }

        String gameUuid = game.getUuid().toString();

        List<PlatformBundleEntry> platforms = Arrays.stream(PatchPlatform.values())
                .map(p -> new PlatformBundleEntry(
                        p.getFormattedName(),
                        bundleFileStoragePort.listBundleFiles(gameUuid, p.getFormattedName())
                ))
                .toList();

        return new LoadGameBundleListOutput(platforms);
    }
}
