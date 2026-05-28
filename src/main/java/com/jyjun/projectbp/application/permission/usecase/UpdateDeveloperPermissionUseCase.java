package com.jyjun.projectbp.application.permission.usecase;

import com.jyjun.projectbp.application.auth.service.LoadCurrentAccountService;
import com.jyjun.projectbp.application.developer.service.LoadDeveloperService;
import com.jyjun.projectbp.application.developer.util.IsRootAccountOfDeveloperUtil;
import com.jyjun.projectbp.application.permission.model.input.UpdateDeveloperPermissionInput;
import com.jyjun.projectbp.application.permission.model.output.UpdateDeveloperPermissionOutput;
import com.jyjun.projectbp.application.permission.service.CreateDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.DeleteDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.service.LoadDeveloperAccessPermissionService;
import com.jyjun.projectbp.application.permission.util.HasDeveloperAccessPermissionUtil;
import com.jyjun.projectbp.domain.developeraccesspermission.enums.DeveloperAccessPermissionType;
import com.jyjun.projectbp.common.exception.AccessDeniedException;
import com.jyjun.projectbp.common.exception.SelfPermissionModifyException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UpdateDeveloperPermissionUseCase {

    private final LoadCurrentAccountService loadCurrentAccountService;
    private final DeleteDeveloperAccessPermissionService deleteDeveloperAccessPermissionService;
    private final CreateDeveloperAccessPermissionService createDeveloperAccessPermissionService;

    private final IsRootAccountOfDeveloperUtil isRootAccountOfDeveloperUtil;
    private final HasDeveloperAccessPermissionUtil hasDeveloperAccessPermissionUtil;

    public UpdateDeveloperPermissionUseCase(
            LoadCurrentAccountService loadCurrentAccountService,
            DeleteDeveloperAccessPermissionService deleteDeveloperAccessPermissionService,
            CreateDeveloperAccessPermissionService createDeveloperAccessPermissionService,
            LoadDeveloperAccessPermissionService loadDeveloperAccessPermissionService,
            LoadDeveloperService loadDeveloperService
    ) {
        this.loadCurrentAccountService = loadCurrentAccountService;
        this.deleteDeveloperAccessPermissionService = deleteDeveloperAccessPermissionService;
        this.createDeveloperAccessPermissionService = createDeveloperAccessPermissionService;

        this.isRootAccountOfDeveloperUtil = new IsRootAccountOfDeveloperUtil(loadDeveloperService);
        this.hasDeveloperAccessPermissionUtil = new HasDeveloperAccessPermissionUtil(loadDeveloperAccessPermissionService);
    }

    @Transactional
    public UpdateDeveloperPermissionOutput execute(UpdateDeveloperPermissionInput input) {
        Long currentAccountId = loadCurrentAccountService.getCurrentAccountId();

        if (currentAccountId.equals(input.accountId())) {
            throw new SelfPermissionModifyException("본인의 권한은 직접 변경할 수 없습니다.");
        }

        if (isRootAccountOfDeveloperUtil.is(currentAccountId, input.developerId())) {
            // 루트 계정이면 통과
        } else if (hasDeveloperAccessPermissionUtil.has(currentAccountId, input.developerId(), DeveloperAccessPermissionType.ADMIN)) {
            // 개발자 ADMIN 권한 있으면 통과
        } else {
            throw new AccessDeniedException("개발자 권한을 관리할 권한이 없습니다.");
        }

        // 모든 권한을 삭제한 뒤 다시 생성함
        deleteDeveloperAccessPermissionService.deleteByAccountIdAndDeveloperId(input.accountId(), input.developerId());

        // 입력받은 권한들을 생성
        List<DeveloperAccessPermissionType> created = input.permissions().stream()
                .map(permission -> createDeveloperAccessPermissionService.create(input.accountId(), input.developerId(), permission).getPermission())
                .toList();

        return new UpdateDeveloperPermissionOutput(input.accountId(), input.developerId(), created);
    }
}
