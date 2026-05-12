package com.jyjun.projectbp.common.exception;

public class SelfPermissionModifyException extends BusinessException {

    public SelfPermissionModifyException(String message) {
        super(403, "SELF_PERMISSION_MODIFY", message);
    }
}
