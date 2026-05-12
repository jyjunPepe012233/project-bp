package com.jyjun.projectbp.application.game.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.model.input.CreateGameInput;
import com.jyjun.projectbp.application.game.model.output.CreateGameOutput;
import com.jyjun.projectbp.application.game.service.CreateGameService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateGameUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final CreateGameService createGameService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;

    public CreateGameUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            CreateGameService createGameService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.createGameService = createGameService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
    }

    @Transactional
    public CreateGameOutput execute(CreateGameInput input) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Long developerId = input.developerId();

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
            // 루트 계정이면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
            // 개발자 ADMIN 권한 있으면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.PUBLISHER)) {
            // 개발자 PUBLISHER 권한 있으면 통과
        } else {
            throw new AccessDeniedException("게임을 등록할 권한이 없습니다.");
        }

        Game game = createGameService.create(input.title(), input.description(), developerId);
        return new CreateGameOutput(game.getId(), game.getUuid(), game.getTitle(), game.getDescription(), game.getDeveloperId());
    }
}
