package com.jyjun.projectbp.common.exception;

public class TokenExpiredException extends BusinessException {

    public TokenExpiredException(String message) {
        super(401, "TOKEN_EXPIRED", message);
    }
}
