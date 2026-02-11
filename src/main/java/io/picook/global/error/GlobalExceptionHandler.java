package io.picook.global.error;

import io.picook.global.common.ApiResponse;
import io.picook.global.common.ValidationError;
import io.picook.global.error.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import javax.validation.ConstraintViolationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * @Valid 검증 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        log.error("Validation failed: {}", e.getMessage());
        List<ValidationError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> ValidationError.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.validationError(ErrorCode.INVALID_INPUT_VALUE, errors));
    }

    /**
     * 파라미터 검증 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiResponse<?>> handleConstraintViolationException(
            ConstraintViolationException e
    ) {
        log.error("Constraint violation: {}", e.getMessage());
        List<ValidationError> errors = e.getConstraintViolations().stream()
                .map(violation -> ValidationError.builder()
                        .field(violation.getPropertyPath().toString())
                        .message(violation.getMessage())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity
                .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
                .body(ApiResponse.validationError(ErrorCode.INVALID_INPUT_VALUE, errors));
    }

    /**
     * JSON 파싱 실패 등 요청 본문이 올바르지 않을 때 발생하는 예외 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        log.warn("handleHttpMessageNotReadableException: {}", e.getMessage());
        return createErrorResponse(ErrorCode.INVALID_REQUEST_BODY);
    }

    /**
     * 타입 변환 실패 시 발생하는 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        log.warn("handleMethodArgumentTypeMismatchException: {}", e.getMessage());
        return createErrorResponse(ErrorCode.INVALID_INPUT_VALUE);
    }

    /**
     * 지원하지 않는 HTTP method 호출 시 발생
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<ApiResponse<?>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        log.warn("handleHttpRequestMethodNotSupportedException: {}", e.getMessage());
        return createErrorResponse(ErrorCode.METHOD_NOT_ALLOWED);
    }

    /**
     * 인증은 되었지만 권한이 없는 경우 발생
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<?>> handleAccessDeniedException(
            AccessDeniedException e
    ) {
        log.warn("handleAccessDeniedException: {}", e.getMessage());
        return createErrorResponse(ErrorCode.HANDLE_ACCESS_DENIED);
    }

    /**
     * 비즈니스 로직 수행 중 발생하는 예외 처리
     */
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiResponse<?>> handleBusinessException(
            BusinessException e
    ) {
        log.error("BusinessException: {}", e.getErrorCode().getMessage());
        return createErrorResponse(e.getErrorCode());
    }

    /**
     * 정의되지 않은 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<?>> handleException(
            Exception e
    ) {
        log.error("Unhandled Exception: ", e);
        return createErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ApiResponse<?>> createErrorResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.error(errorCode));
    }
}
