package com.jyjun.projectbp.common.exception;

public class InvalidPasswordException extends BusinessException {

    public InvalidPasswordException(String message) {
        super(401, "INVALID_PASSWORD", message);
    }
}
