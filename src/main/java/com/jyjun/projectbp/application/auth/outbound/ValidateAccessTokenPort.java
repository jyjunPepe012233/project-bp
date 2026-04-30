package com.jyjun.projectbp.application.auth.outbound;

public interface ValidateAccessTokenPort {

    Long extractAccountId(String accessToken);
}
