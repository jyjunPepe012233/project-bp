package com.jyjun.projectbp.application.account.model.input;

import com.jyjun.projectbp.application.account.model.entry.DeveloperAccessPermissionEntry;
import com.jyjun.projectbp.application.account.model.entry.GameAccessPermissionEntry;
import com.jyjun.projectbp.common.validation.ValidationConstants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record CreateAccountInput(
        @NotBlank(message = "계정명을 입력해 주세요.")
        @Pattern(regexp = ValidationConstants.ACCOUNT_NAME_PATTERN, message = ValidationConstants.ACCOUNT_NAME_MESSAGE)
        String name,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Pattern(regexp = ValidationConstants.PASSWORD_PATTERN, message = ValidationConstants.PASSWORD_MESSAGE)
        String password,

        @Valid
        List<DeveloperAccessPermissionEntry> developerAccessPermissions,

        @Valid
        List<GameAccessPermissionEntry> gameAccessPermissions
) {
}
