package com.grantip.backend.global.exception;

import com.grantip.backend.global.code.ErrorCode;
import com.grantip.backend.global.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(CustomException.class)
  protected final ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    log.error("Exception 발생: {}", e.getErrorCode().getMessage(), e);
    String message = e.getCustomMessage() != null ? e.getCustomMessage() : e.getErrorCode().getMessage();


    ErrorResponse response = ErrorResponse.builder()
        .code(e.getErrorCode().getHttpStatus().value())
        .error(e.getErrorCode().getHttpStatus().name())
        .message(message)
        .build();

    return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected final ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e){
    log.error("Exception 발생: {}", e.getMessage(), e);

    String errorMessage = e.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(error -> error.getField() + ": "+ error.getDefaultMessage())
        .distinct()
        .collect(Collectors.joining(", "));

    ErrorResponse response = ErrorResponse.builder()
        .code(ErrorCode.BAD_REQUEST.getHttpStatus().value())
        .error(ErrorCode.BAD_REQUEST.getHttpStatus().name())
        .message(errorMessage)
        .build();

    return ResponseEntity.status(ErrorCode.BAD_REQUEST.getHttpStatus()).body(response);
  }

  @ExceptionHandler(Exception.class)
  protected final ResponseEntity<ErrorResponse> handleGeneralException(Exception e){
    log.error("Exception 발생: {}", e.getMessage(), e);

    ErrorResponse response = ErrorResponse.builder()
        .code(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value())
        .error(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().name())
        .message(ErrorCode.INTERNAL_SERVER_ERROR.getMessage())
        .build();

    return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
  }
}
