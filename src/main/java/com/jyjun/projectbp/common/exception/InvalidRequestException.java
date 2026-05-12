package com.jyjun.projectbp.common.exception;

public class InvalidRequestException extends BusinessException {

    public InvalidRequestException(String message) {
        super(400, "INVALID_REQUEST", message);
    }
}
