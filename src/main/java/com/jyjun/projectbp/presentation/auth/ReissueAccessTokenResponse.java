package com.jyjun.projectbp.presentation.auth;

import com.jyjun.projectbp.application.auth.model.output.ReissueAccessTokenOutput;

public record ReissueAccessTokenResponse(String accessToken, String refreshToken) {

    public ReissueAccessTokenResponse(ReissueAccessTokenOutput output) {
        this(output.accessToken(), output.refreshToken());
    }
}
