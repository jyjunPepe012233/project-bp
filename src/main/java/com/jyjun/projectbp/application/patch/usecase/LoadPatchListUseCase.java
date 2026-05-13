package com.jyjun.projectbp.application.patch.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.patch.model.output.LoadPatchOutput;
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

import java.util.List;

@Service
public class LoadPatchListUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadGameService loadGameService;
    private final LoadPatchService loadPatchService;
    private final AddressableFileStoragePort addressableFileStoragePort;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;
    private final HasGameAccessPermissionUtil hasGameAccessPermissionUtil;

    public LoadPatchListUseCase(
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

    public List<LoadPatchOutput> execute(Long gameId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Game game = loadGameService.loadByIdOrThrow(gameId);
        Long developerId = game.getDeveloperId();

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
            // 루트 계정이면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
            // 개발자 ADMIN 권한 있으면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.PUBLISHER)) {
            // 개발자 PUBLISHER 권한 있으면 통과
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.ADMIN)) {
            // 게임 ADMIN 권한 있으면 통과
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.PRIMARY_WRITE)) {
            // 게임 PRIMARY_WRITE 권한 있으면 통과
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.MAINTAIN)) {
            // 게임 MAINTAIN 권한 있으면 통과
        } else {
            throw new AccessDeniedException("패치 목록을 조회할 권한이 없습니다.");
        }

        String gameUuid = game.getUuid().toString();

        return loadPatchService.loadByGameId(gameId).stream()
                .map(p -> {
                    String platform = p.getPlatform().getFormattedName();
                    boolean catalogUploaded = addressableFileStoragePort.catalogExists(gameUuid, p.getVersion(), platform);
                    boolean catalogHashUploaded = addressableFileStoragePort.catalogHashExists(gameUuid, p.getVersion(), platform);

                    return new LoadPatchOutput(
                            p.getId(),
                            p.getGameId(),
                            p.getVersion(),
                            p.getPlatform(),
                            p.getPatchNote(),
                            catalogUploaded,
                            catalogHashUploaded,
                            p.getCreatedAt()
                    );
                })
                .toList();
    }
}
