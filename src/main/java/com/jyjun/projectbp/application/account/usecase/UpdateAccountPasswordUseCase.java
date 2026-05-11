package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.model.input.UpdateAccountPasswordInput;
import com.jyjun.projectbp.application.account.service.UpdateAccountPasswordService;
import com.jyjun.projectbp.application.account.util.IsManagerOfDeveloperAccountUtil;
import com.jyjun.projectbp.application.account.util.IsManagerOfGameAccountUtil;
import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateAccountPasswordUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final UpdateAccountPasswordService updateAccountPasswordService;

    private final IsManagerOfDeveloperAccountUtil isManagerOfDeveloperAccountUtil;
    private final IsManagerOfGameAccountUtil isManagerOfGameAccountUtil;

    public UpdateAccountPasswordUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            UpdateAccountPasswordService updateAccountPasswordService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService,
            LoadGameService loadGameService,
            LoadDeveloperService loadDeveloperService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.updateAccountPasswordService = updateAccountPasswordService;

        this.isManagerOfDeveloperAccountUtil = new IsManagerOfDeveloperAccountUtil(loadDeveloperAccessPermissionService, loadDeveloperService);
        this.isManagerOfGameAccountUtil = new IsManagerOfGameAccountUtil(loadGameAccessPermissionService, loadGameService, loadDeveloperService, loadDeveloperAccessPermissionService);
    }

    @Transactional
    public void execute(UpdateAccountPasswordInput input) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        if (isManagerOfDeveloperAccountUtil.is(currentAccountId, input.accountId())) {
            // 개발자 단위 계정 관리 권한 있으면 통과
        } else if (isManagerOfGameAccountUtil.is(currentAccountId, input.accountId())) {
            // 게임 단위 계정 관리 권한 있으면 통과
        } else {
            throw new IllegalArgumentException("계정의 비밀번호를 수정할 권한이 없습니다.");
        }

        updateAccountPasswordService.updatePassword(input.accountId(), input.password());
    }
}
