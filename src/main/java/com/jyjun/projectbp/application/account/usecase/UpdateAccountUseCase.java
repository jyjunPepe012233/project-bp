package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.input.UpdateAccountInput;
import com.jyjun.projectbp.application.account.model.output.UpdateAccountOutput;
import com.jyjun.projectbp.application.account.service.UpdateAccountService;
import com.jyjun.projectbp.application.account.util.IsManagerOfGameAccountUtil;
import com.jyjun.projectbp.application.account.util.IsManagerOfDeveloperAccountUtil;
import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.domain.account.model.Account;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateAccountUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final UpdateAccountService updateAccountService;

    private final IsManagerOfDeveloperAccountUtil isRootOrDeveloperAdminUtil;
    private final IsManagerOfGameAccountUtil isRootOrDeveloperAdminOrGameAdminUtil;

    public UpdateAccountUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            UpdateAccountService updateAccountService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService,
            LoadGameService loadGameService,
            LoadDeveloperService loadDeveloperService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.updateAccountService = updateAccountService;

        this.isRootOrDeveloperAdminUtil = new IsManagerOfDeveloperAccountUtil(loadDeveloperAccessPermissionService, loadDeveloperService);
        this.isRootOrDeveloperAdminOrGameAdminUtil = new IsManagerOfGameAccountUtil(loadGameAccessPermissionService, loadGameService, loadDeveloperService, loadDeveloperAccessPermissionService);
    }

    @Transactional
    public UpdateAccountOutput execute(UpdateAccountInput input) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        if (isRootOrDeveloperAdminUtil.is(currentAccountId, input.accountId())) {
            // 개발자 단위 계정 관리 권한 있으면 통과
        } else if (isRootOrDeveloperAdminOrGameAdminUtil.is(currentAccountId, input.accountId())) {
            // 게임 단위 계정 관리 권한 있으면 통과
        } else {
            throw new IllegalArgumentException("계정을 수정할 권한이 없습니다.");
        }

        Account updated = updateAccountService.updateName(input.accountId(), input.name());
        return new UpdateAccountOutput(updated.getId(), updated.getName());
    }
}
