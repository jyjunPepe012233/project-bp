package com.jyjun.projectbp.presentation;

import com.jyjun.projectbp.common.dto.ErrorResponse;
import com.jyjun.projectbp.common.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse body = new ErrorResponse(e.getStatus(), e.getError(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getMessage();
        ErrorResponse body = new ErrorResponse(400, "VALIDATION_ERROR", message); // error 필드를 임의로 정함
        return ResponseEntity.badRequest().body(body);
    }
}
