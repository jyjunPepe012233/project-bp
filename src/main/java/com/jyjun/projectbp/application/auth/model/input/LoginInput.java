package com.jyjun.projectbp.application.auth.model.input;

import jakarta.validation.constraints.NotBlank;

public record LoginInput(
        @NotBlank(message = "계정명을 입력해 주세요.")
        String name,
        @NotBlank(message = "비밀번호를 입력해 주세요.")
        String password
) {
}
