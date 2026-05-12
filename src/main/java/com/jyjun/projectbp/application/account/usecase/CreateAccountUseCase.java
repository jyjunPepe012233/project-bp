package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.entry.DeveloperAccessPermissionEntry;
import com.jyjun.projectbp.application.account.model.entry.GameAccessPermissionEntry;
import com.jyjun.projectbp.application.account.model.input.CreateAccountInput;
import com.jyjun.projectbp.application.account.model.output.CreateAccountOutput;
import com.jyjun.projectbp.application.account.service.CreateAccountService;
import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.CreateDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.CreateGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.application.permission.util.HasGameAccessPermissionUtil;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.common.exception.InvalidRequestException;
import com.jyjun.projectbp.domain.account.model.Account;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.game.model.Game;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CreateAccountUseCase {

    private final CreateAccountService createAccountService;
    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadGameService loadGameService;
    private final CreateDeveloperAccessPermissionService createDeveloperAccessPermissionService;
    private final CreateGameAccessPermissionService createGameAccessPermissionService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;
    private final HasGameAccessPermissionUtil hasGameAccessPermissionUtil;

    public CreateAccountUseCase(
            CreateAccountService createAccountService,
            LoadCurrentAccountService loadCurrentAccountService,
            LoadGameService loadGameService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService,
            CreateDeveloperAccessPermissionService createDeveloperAccessPermissionService,
            CreateGameAccessPermissionService createGameAccessPermissionService
    ) {
        this.createAccountService = createAccountService;
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadGameService = loadGameService;
        this.createDeveloperAccessPermissionService = createDeveloperAccessPermissionService;
        this.createGameAccessPermissionService = createGameAccessPermissionService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
        this.hasGameAccessPermissionUtil = new HasGameAccessPermissionUtil(loadGameAccessPermissionService);
    }

    @Transactional
    public CreateAccountOutput execute(CreateAccountInput input) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        if (input.developerAccessPermissions().isEmpty() && input.gameAccessPermissions().isEmpty()) {
            throw new InvalidRequestException("계정에는 최소 하나 이상의 권한이 필요합니다.");
        }

        // 계정을 먼저 생성하고, 해당 계정이 어떤 Developer나 Game에 연결할지는 아래 루프에서 검증하면서 연결할 것임
        Account created = createAccountService.create(input.name(), input.password());

        // 각 개발자 접근 권한에 대해, 해당 권한을 부여할 수 있는지 검증하는 루프
        for (DeveloperAccessPermissionEntry entry : input.developerAccessPermissions()) {
            Long developerId = entry.developerId();

            if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
                // 루트 계정이면 통과
            } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
                // 개발자 ADMIN 권한 있으면 통과
            } else {
                throw new AccessDeniedException("개발자 접근 권한을 부여할 권한이 없습니다. (개발자 ID: " + developerId + ")");
            }

            for (DeveloperAccessPermissionType permission : entry.permissions()) {
                createDeveloperAccessPermissionService.create(created.getId(), developerId, permission);
            }
        }

        // 각 게임 접근 권한에 대해, 해당 게임의 권한을 사용자가 부여할 수 있는지 검증하는 루프
        for (GameAccessPermissionEntry entry : input.gameAccessPermissions()) {
            Game game = loadGameService.loadByIdOrThrow(entry.gameId());
            Long developerId = game.getDeveloperId();

            if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
                // 루트 계정이면 통과
            } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
                // 개발자 ADMIN 권한 있으면 통과
            } else if (hasGameAccessPermissionUtil.has(currentAccountId, entry.gameId(), GameAccessPermissionType.ADMIN)) {
                // 게임 ADMIN 권한 있으면 통과
            } else {
                throw new AccessDeniedException("게임 접근 권한을 부여할 권한이 없습니다. (게임 ID: " + entry.gameId() + ")");
            }

            for (GameAccessPermissionType permission : entry.permissions()) {
                createGameAccessPermissionService.create(created.getId(), entry.gameId(), permission);
            }
        }

        return new CreateAccountOutput(created.getName());
    }
}
