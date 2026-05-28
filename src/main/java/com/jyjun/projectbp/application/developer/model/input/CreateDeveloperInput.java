package com.jyjun.projectbp.application.developer.model.input;

import com.jyjun.projectbp.common.validation.ValidationConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateDeveloperInput(
        @NotBlank(message = "개발사 이름을 입력해 주세요.")
        @Pattern(regexp = ValidationConstants.DISPLAY_NAME_PATTERN, message = ValidationConstants.DISPLAY_NAME_MESSAGE)
        String developerName,

        @NotBlank(message = "루트 계정명을 입력해 주세요.")
        @Pattern(regexp = ValidationConstants.ACCOUNT_NAME_PATTERN, message = ValidationConstants.ACCOUNT_NAME_MESSAGE)
        String rootAccountName,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        @Pattern(regexp = ValidationConstants.PASSWORD_PATTERN, message = ValidationConstants.PASSWORD_MESSAGE)
        String rootAccountPassword
) {
}
