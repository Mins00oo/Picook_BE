package io.picook.global.common;

import io.picook.global.error.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ApiResponse<T> {
    private static final String SUCCESS_CODE = "S000";
    private static final String SUCCESS_MESSAGE = "요청이 성공적으로 처리되었습니다.";

    private final boolean success;
    private final String code;
    private final T data;
    private final String message;
    private final Instant timestamp;
    private final List<ValidationError> errors;

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .code(SUCCESS_CODE)
                .data(data)
                .message(SUCCESS_MESSAGE)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> error(ErrorCode errorCode) {
        return ApiResponse.<Void>builder()
                .success(false)
                .code(errorCode.getCode())
                .data(null)
                .message(errorCode.getMessage())
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> validationError(ErrorCode errorCode, List<ValidationError> errors) {
        return ApiResponse.<Void>builder()
                .success(false)
                .code(errorCode.getCode())
                .data(null)
                .message(errorCode.getMessage())
                .timestamp(Instant.now())
                .errors(errors)
                .build();
    }
}
