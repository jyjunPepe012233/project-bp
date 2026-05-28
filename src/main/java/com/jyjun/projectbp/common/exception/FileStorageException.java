package com.jyjun.projectbp.common.exception;

public class FileStorageException extends BusinessException {

    public FileStorageException(String message, Throwable cause) {
        super(500, "FILE_STORAGE_ERROR", message, cause);
    }
}
