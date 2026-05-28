package com.jyjun.projectbp.common.exception;

public class InvalidPathException extends BusinessException {

    public InvalidPathException(String message) {
        super(400, "INVALID_PATH", message);
    }
}
