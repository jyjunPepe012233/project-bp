package com.jyjun.projectbp.application.developer.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.model.input.UpdateDeveloperInput;
import com.jyjun.projectbp.application.developer.model.output.UpdateDeveloperOutput;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.service.UpdateDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.domain.developer.model.Developer;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateDeveloperUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final UpdateDeveloperService updateDeveloperService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;

    public UpdateDeveloperUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            UpdateDeveloperService updateDeveloperService,
            LoadDeveloperService loadDeveloperService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.updateDeveloperService = updateDeveloperService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
    }

    @Transactional
    public UpdateDeveloperOutput execute(UpdateDeveloperInput input) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();
        Long developerId = input.developerId();

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, developerId)) {
            // 루트 계정이면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, developerId, DeveloperAccessPermissionType.ADMIN)) {
            // 개발자 ADMIN 권한 있으면 통과
        } else {
            throw new IllegalArgumentException("개발자 정보를 수정할 권한이 없습니다.");
        }

        Developer updated = updateDeveloperService.updateName(developerId, input.name());
        return new UpdateDeveloperOutput(updated.getId(), updated.getName());
    }
}
