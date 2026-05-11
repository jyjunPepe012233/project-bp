package com.jyjun.projectbp.application.permission.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.model.input.UpdateGamePermissionInput;
import com.jyjun.projectbp.application.permission.model.output.UpdateGamePermissionOutput;
import com.jyjun.projectbp.application.permission.service.CreateGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.DeleteGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.application.permission.util.HasGameAccessPermissionUtil;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.domain.gameaccesspermission.enums.GameAccessPermissionType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateGamePermissionUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadGameService loadGameService;
    private final DeleteGameAccessPermissionService deleteGameAccessPermissionService;
    private final CreateGameAccessPermissionService createGameAccessPermissionService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;
    private final HasGameAccessPermissionUtil hasGameAccessPermissionUtil;

    public UpdateGamePermissionUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadGameService loadGameService,
            DeleteGameAccessPermissionService deleteGameAccessPermissionService,
            CreateGameAccessPermissionService createGameAccessPermissionService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadGameService = loadGameService;
        this.deleteGameAccessPermissionService = deleteGameAccessPermissionService;
        this.createGameAccessPermissionService = createGameAccessPermissionService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
        this.hasGameAccessPermissionUtil = new HasGameAccessPermissionUtil(loadGameAccessPermissionService);
    }

    @Transactional
    public UpdateGamePermissionOutput execute(UpdateGamePermissionInput input) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        if (currentAccountId.equals(input.accountId())) {
            throw new IllegalArgumentException("본인의 권한은 직접 변경할 수 없습니다.");
        }

        Long developerId = loadGameService.loadByIdOrThrow(input.gameId()).getDeveloperId();

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
            // 루트 계정이면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
            // 개발자 ADMIN 권한 있으면 통과
        } else if (hasGameAccessPermissionUtil.has(currentAccountId, input.gameId(), GameAccessPermissionType.ADMIN)) {
            // 게임 ADMIN 권한 있으면 통과
        } else {
            throw new IllegalArgumentException("게임 권한을 관리할 권한이 없습니다.");
        }

        deleteGameAccessPermissionService.deleteByAccountIdAndGameId(input.accountId(), input.gameId());

        List<GameAccessPermissionType> created = input.permissions().stream()
                .map(permission -> createGameAccessPermissionService.create(input.accountId(), input.gameId(), permission).getPermission())
                .toList();

        return new UpdateGamePermissionOutput(input.accountId(), input.gameId(), created);
    }
}
