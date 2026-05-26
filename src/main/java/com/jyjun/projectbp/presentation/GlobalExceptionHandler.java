package com.jyjun.projectbp.presentation;

import com.jyjun.projectbp.common.dto.ErrorResponse;
import com.jyjun.projectbp.common.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        ErrorResponse body = new ErrorResponse(e.getStatus(), e.getError(), e.getMessage());
        return ResponseEntity.status(e.getStatus()).body(body);
    }

    // DTO 검증 실패 시 발생하는 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getDefaultMessage())
                .collect(Collectors.joining(", ")); // 필드 오류를 콤마로 구분하여 하나의 문자열로 통합하는 작업. 어떤 필드가 오류인지 응답으로 확인 가능함!
        ErrorResponse body = new ErrorResponse(400, "VALIDATION_ERROR", message);
        return ResponseEntity.badRequest().body(body);
    }

    // 여러 util 클래스에서 no such element exception을 던지는 경우가 있어서 예외처리 (Not found)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NoSuchElementException e) {
        ErrorResponse body = new ErrorResponse(404, "NOT_FOUND", e.getMessage());
        return ResponseEntity.status(404).body(body);
    }
}

// TODO: 5월 13일 0시 55분 다음 할 일 메모
//   지금 입력 검증(백에서는 Validation 추가, 프론트에서는 입력 조건 추가) 하는 중
//   또, catalog의 이름을 고정하여 patch 테이블에 파일 이름을 저장하는 컬럼을 모두 삭제하고, 실제 파일을 조회하여 패치 파일 업로드 여부를 판정하도록 변경하는 작업 중