package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.output.LoadAccountOutput;
import com.jyjun.projectbp.application.account.util.IsManagerOfDeveloperAccountUtil;
import com.jyjun.projectbp.application.account.util.IsManagerOfGameAccountUtil;
import com.jyjun.projectbp.application.auth.service.LoadAccountService;
import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.account.model.Account;
import org.springframework.stereotype.Service;

@Service
public class LoadAccountUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadAccountService loadAccountService;

    private final IsManagerOfDeveloperAccountUtil isManagerOfDeveloperAccountUtil;
    private final IsManagerOfGameAccountUtil isManagerOfGameAccountUtil;

    public LoadAccountUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadAccountService loadAccountService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService,
            LoadGameService loadGameService,
            LoadDeveloperService loadDeveloperService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadAccountService = loadAccountService;

        this.isManagerOfDeveloperAccountUtil = new IsManagerOfDeveloperAccountUtil(loadDeveloperAccessPermissionService, loadDeveloperService);
        this.isManagerOfGameAccountUtil = new IsManagerOfGameAccountUtil(loadGameAccessPermissionService, loadGameService, loadDeveloperService, loadDeveloperAccessPermissionService);
    }

    public LoadAccountOutput execute(Long accountId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        if (currentAccountId.equals(accountId)) {
            // 본인이면 통과
        } else if (isManagerOfDeveloperAccountUtil.is(currentAccountId, accountId)) {
            // 개발자 단위 계정 관리 권한 있으면 통과
        } else if (isManagerOfGameAccountUtil.is(currentAccountId, accountId)) {
            // 게임 단위 계정 관리 권한 있으면 통과
        } else {
            throw new IllegalArgumentException("계정 정보를 조회할 권한이 없습니다.");
        }

        Account account = loadAccountService.loadByIdOrThrow(accountId);
        return new LoadAccountOutput(account.getId(), account.getName());
    }
}
