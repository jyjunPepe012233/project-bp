package com.jyjun.projectbp.application.auth.model.output;

public record ReissueAccessTokenOutput(
        String accessToken,
        String refreshToken // refresh token rotation 적응 중이므로 새로운 refresh token도 함께 반환하도록 함
) {
}
