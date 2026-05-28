package com.jyjun.projectbp.common.exception;

public class TokenReuseDetectedException extends BusinessException {

    public TokenReuseDetectedException(String message) {
        super(401, "TOKEN_REUSE_DETECTED", message);
    }
}
