package com.jyjun.projectbp.presentation.auth;

import com.jyjun.projectbp.application.auth.model.output.LoginOutput;

public record LoginResponse(String accessToken, String refreshToken) {

    public LoginResponse(LoginOutput output) {
        this(output.accessToken(), output.refreshToken());
    }
}
