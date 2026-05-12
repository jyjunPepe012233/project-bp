package com.jyjun.projectbp.common.exception;

public class AuthenticationFailedException extends BusinessException {

    public AuthenticationFailedException(String message) {
        super(401, "AUTHENTICATION_FAILED", message);
    }
}
