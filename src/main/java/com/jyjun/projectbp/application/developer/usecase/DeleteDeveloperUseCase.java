package com.jyjun.projectbp.application.developer.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.DeleteDeveloperService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.domain.developer.model.Developer;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class DeleteDeveloperUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final LoadDeveloperService loadDeveloperService;
    private final DeleteDeveloperService deleteDeveloperService;
    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;

    public DeleteDeveloperUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            LoadDeveloperService loadDeveloperService,
            DeleteDeveloperService deleteDeveloperService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.loadDeveloperService = loadDeveloperService;
        this.deleteDeveloperService = deleteDeveloperService;
        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
    }

    @Transactional
    public void execute(Long developerId) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        if (!isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
            throw new AccessDeniedException("개발자를 삭제할 권한이 없습니다.");
        }

        Developer developer = loadDeveloperService.loadByIdOrThrow(developerId);
        deleteDeveloperService.delete(developerId, developer.getRootAccountId());
    }
}
