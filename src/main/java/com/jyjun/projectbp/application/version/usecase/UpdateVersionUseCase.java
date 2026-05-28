package com.jyjun.projectbp.application.version.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.patch.service.LoadPatchService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.application.permission.util.HasGameAccessPermissionUtil;
import com.jyjun.projectbp.application.version.model.input.UpdateVersionInput;
import com.jyjun.projectbp.application.version.model.output.UpdateVersionOutput;
import com.jyjun.projectbp.application.version.service.SaveVersionService;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.common.exception.InvalidRequestException;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import com.jyjun.projectbp.domain.patch.model.Patch;
import com.jyjun.projectbp.domain.version.model.Version;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateVersionUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadGameService loadGameService;
    private final LoadPatchService loadPatchService;
    private final SaveVersionService saveVersionService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;
    private final HasGameAccessPermissionUtil hasGameAccessPermissionUtil;

    public UpdateVersionUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadGameService loadGameService,
            LoadPatchService loadPatchService,
            SaveVersionService saveVersionService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadGameService = loadGameService;
        this.loadPatchService = loadPatchService;
        this.saveVersionService = saveVersionService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
        this.hasGameAccessPermissionUtil = new HasGameAccessPermissionUtil(loadGameAccessPermissionService);
    }

    @Transactional
    public UpdateVersionOutput execute(UpdateVersionInput input) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Long gameId = input.gameId();

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
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, gameId, GameAccessPermissionType.MAINTAIN)) {
            // 게임 MAINTAIN 권한 있으면 통과
        } else {
            throw new AccessDeniedException("버전을 설정할 권한이 없습니다.");
        }

        Long patchId = input.patchId();
        if (patchId != null) {
            Patch patch = loadPatchService.loadByIdOrThrow(patchId);
            if (!patch.getGameId().equals(gameId)) {
                throw new InvalidRequestException("해당 게임의 patchId만 버전으로 설정할 수 있습니다.");
            }
        }

        Version saved = saveVersionService.save(gameId, patchId);
        return new UpdateVersionOutput(saved.getGameId(), saved.getPatchId());
    }
}
