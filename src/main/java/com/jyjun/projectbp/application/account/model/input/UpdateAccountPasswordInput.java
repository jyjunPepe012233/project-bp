package com.jyjun.projectbp.application.account.model.input;

import com.jyjun.projectbp.common.validation.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateAccountPasswordInput(
        Long accountId,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Pattern(regexp = ValidationConstants.PASSWORD_PATTERN, message = ValidationConstants.PASSWORD_MESSAGE)
        String password
) {
}
