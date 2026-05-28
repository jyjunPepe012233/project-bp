package com.jyjun.projectbp.application.account.usecase;

import com.jyjun.projectbp.application.account.service.DeleteAccountService;
import com.jyjun.projectbp.application.account.util.IsManagerOfDeveloperAccountUtil;
import com.jyjun.projectbp.application.account.util.IsManagerOfGameAccountUtil;
import com.jyjun.projectbp.application.auth.service.LoadAccountService;
import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.game.service.LoadGameService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadGameAccessPermissionService;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.common.exception.InvalidRequestException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class DeleteAccountUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadAccountService loadAccountService;
    private final LoadDeveloperService loadDeveloperService;
    private final DeleteAccountService deleteAccountService;

    private final IsManagerOfDeveloperAccountUtil isRootOrDeveloperAdminUtil;
    private final IsManagerOfGameAccountUtil isRootOrDeveloperAdminOrGameAdminUtil;

    public DeleteAccountUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadAccountService loadAccountService,
            LoadDeveloperService loadDeveloperService,
            DeleteAccountService deleteAccountService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadGameAccessPermissionService loadGameAccessPermissionService,
            LoadGameService loadGameService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadAccountService = loadAccountService;
        this.loadDeveloperService = loadDeveloperService;
        this.deleteAccountService = deleteAccountService;

        this.isRootOrDeveloperAdminUtil = new IsManagerOfDeveloperAccountUtil(
                loadDeveloperAccessPermissionService,
                loadDeveloperService
        );
        this.isRootOrDeveloperAdminOrGameAdminUtil = new IsManagerOfGameAccountUtil(
                loadGameAccessPermissionService,
                loadGameService,
                loadDeveloperService,
                loadDeveloperAccessPermissionService
        );
    }

    @Transactional
    public void execute(Long targetAccountId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        loadAccountService.loadByIdOrThrow(targetAccountId);

        if (currentAccountId.equals(targetAccountId)) {
            throw new InvalidRequestException("자기 자신의 계정은 삭제할 수 없습니다.");
        }

        if (isRootAccount(targetAccountId)) {
            throw new InvalidRequestException("루트 계정은 단독으로 삭제할 수 없습니다. 개발자를 삭제해 주세요.");
        }

        if (isRootOrDeveloperAdminUtil.is(currentAccountId, targetAccountId)) {
            // 개발자 단위 계정 관리 권한 있으면 통과
        } else if (isRootOrDeveloperAdminOrGameAdminUtil.is(currentAccountId, targetAccountId)) {
            // 게임 단위 계정 관리 권한 있으면 통과
        } else {
            throw new AccessDeniedException("계정을 삭제할 권한이 없습니다.");
        }

        deleteAccountService.deleteById(targetAccountId);
    }

    private boolean isRootAccount(Long accountId) {
        try {
            loadDeveloperService.loadByRootAccountIdOrThrow(accountId);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
