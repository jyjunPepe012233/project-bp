package com.jyjun.projectbp.common.exception;

public class DuplicateResourceException extends BusinessException {

    public DuplicateResourceException(String message) {
        super(409, "DUPLICATE_RESOURCE", message);
    }
}
