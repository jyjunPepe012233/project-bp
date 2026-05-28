package com.jyjun.projectbp.application.auth.model.input;

import jakarta.validation.constraints.NotBlank;

public record ReissueAccessTokenInput(
        @NotBlank(message = "리프레시 토큰이 필요합니다.")
        String refreshToken
) {
}
