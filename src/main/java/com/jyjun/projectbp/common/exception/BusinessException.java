package com.jyjun.projectbp.common.exception;

// 사실 business exception은 아니고 그냥 custom exception에 가깝게 사용되고 있긴 함
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
