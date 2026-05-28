package com.jyjun.projectbp.common.dto;

public record ErrorResponse(
        int status,
        String error,
        String message
) {
}
