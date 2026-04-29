package com.jyjun.projectbp.common.dto;

public record ResponseData<T>(
        T data,
        String message
) {

    public ResponseData(T data) {
        this(data, null);
    }
}
