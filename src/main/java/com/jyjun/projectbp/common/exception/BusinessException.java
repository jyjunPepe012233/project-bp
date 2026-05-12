package com.jyjun.projectbp.common.exception;

public abstract class BusinessException extends RuntimeException {

    private final int status;
    private final String error;

    protected BusinessException(int status, String error, String message) {
        super(message);
        this.status = status;
        this.error = error;
    }

    protected BusinessException(int status, String error, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.error = error;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }
}
