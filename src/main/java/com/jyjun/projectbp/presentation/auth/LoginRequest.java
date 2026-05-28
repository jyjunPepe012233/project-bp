package com.jyjun.projectbp.presentation.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "계정명을 입력해 주세요.")
        String name,

        @NotBlank(message = "비밀번호를 입력해 주세요.")
        String password
) {
}
