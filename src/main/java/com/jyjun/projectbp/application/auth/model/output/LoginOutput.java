package com.jyjun.projectbp.application.auth.model.output;

public record LoginOutput(
        String accessToken,
        String refreshToken
) {
}
