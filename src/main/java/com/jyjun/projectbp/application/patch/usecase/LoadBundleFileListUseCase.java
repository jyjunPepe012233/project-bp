package com.jyjun.projectbp.application.patch.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.patch.model.output.LoadBundleFileListOutput;
import com.jyjun.projectbp.application.patch.service.LoadBundleFileListService;
import com.jyjun.projectbp.application.patch.service.LoadPatchService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.application.permission.util.HasGameAccessPermissionUtil;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.patch.model.Patch;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoadBundleFileListUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadPatchService loadPatchService;
    private final LoadGameService loadGameService;
    private final LoadBundleFileListService loadBundleFileListService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;
    private final HasGameAccessPermissionUtil hasGameAccessPermissionUtil;

    public LoadBundleFileListUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadPatchService loadPatchService,
            LoadGameService loadGameService,
            LoadBundleFileListService loadBundleFileListService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadPatchService = loadPatchService;
        this.loadGameService = loadGameService;
        this.loadBundleFileListService = loadBundleFileListService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
        this.hasGameAccessPermissionUtil = new HasGameAccessPermissionUtil(loadGameAccessPermissionService);
    }

    public LoadBundleFileListOutput execute(Long patchId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Patch patch = loadPatchService.loadByIdOrThrow(patchId);
        Game game = loadGameService.loadByIdOrThrow(patch.getGameId());
        Long developerId = game.getDeveloperId();
        Long gameId = game.getId();

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
            // 루트 계정이면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
            // 개발자 ADMIN 권한 있으면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.PUBLISHER)) {
            // 개발자 PUBLISHER 권한 있으면 통과
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.ADMIN)) {
            // 게임 ADMIN 권한 있으면 통과
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.MAINTAIN)) {
            // 게임 MAINTAIN 권한 있으면 통과
        } else {
            throw new IllegalArgumentException("번들 파일 목록을 조회할 권한이 없습니다.");
        }

        List<String> filenames = loadBundleFileListService.load(
                game.getUuid().toString(),
                patch.getVersion(),
                patch.getPlatform()
        );

        return new LoadBundleFileListOutput(filenames);
    }
}
