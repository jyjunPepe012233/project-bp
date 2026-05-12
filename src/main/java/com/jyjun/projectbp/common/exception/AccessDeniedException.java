package com.jyjun.projectbp.common.exception;

public class AccessDeniedException extends BusinessException {

    public AccessDeniedException(String message) {
        super(403, "ACCESS_DENIED", message);
    }
}
