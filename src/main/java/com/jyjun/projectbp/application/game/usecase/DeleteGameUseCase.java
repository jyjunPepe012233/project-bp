package com.jyjun.projectbp.application.game.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.DeleteGameService;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteGameUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadGameService loadGameService;
    private final DeleteGameService deleteGameService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;

    public DeleteGameUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadGameService loadGameService,
            DeleteGameService deleteGameService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadGameService = loadGameService;
        this.deleteGameService = deleteGameService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
    }

    @Transactional
    public void execute(Long gameId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Game game = loadGameService.loadByIdOrThrow(gameId);
        Long developerId = game.getDeveloperId();

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
            // 루트 계정이면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
            // 개발자 ADMIN 권한 있으면 통과
        } else {
            throw new AccessDeniedException("게임을 삭제할 권한이 없습니다.");
        }

        deleteGameService.delete(gameId, game.getUuid().toString());
    }
}
