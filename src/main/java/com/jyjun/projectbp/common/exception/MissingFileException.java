package com.jyjun.projectbp.common.exception;

public class MissingFileException extends BusinessException {

    public MissingFileException(String message) {
        super(400, "MISSING_FILE", message);
    }
}
