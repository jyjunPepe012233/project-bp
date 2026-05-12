package com.jyjun.projectbp.common.exception;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message) {
        super(401, "INVALID_TOKEN", message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(401, "INVALID_TOKEN", message, cause);
    }
}
