package io.picook.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 1. Common (C)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "입력값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C003", "서버 내부 오류가 발생했습니다."),
    HANDLE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C004", "권한이 없습니다."),
    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "C005", "요청 본문이 올바르지 않습니다."),

    // 2. Recipe (R)
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "R001", "존재하지 않는 레시피입니다."),
    INVALID_INGREDIENT_LIST(HttpStatus.BAD_REQUEST, "R002", "유효하지 않은 재료 목록입니다."),

    // 3. AI & Batch (A)
    AI_TRANSFORM_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "A001", "AI 데이터 정제에 실패했습니다."),
    BATCH_EXECUTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "A002", "데이터 적재 작업 중 오류가 발생했습니다."),

    // 4. Auth (U)
    INVALID_SOCIAL_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "U001", "소셜 액세스 토큰이 유효하지 않습니다."),
    SOCIAL_PROVIDER_API_ERROR(HttpStatus.BAD_GATEWAY, "U002", "소셜 제공자 API 호출에 실패했습니다."),
    JWT_CONFIGURATION_INVALID(HttpStatus.INTERNAL_SERVER_ERROR, "U003", "JWT 설정이 올바르지 않습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "U004", "리프레시 토큰이 유효하지 않습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "U005", "액세스 토큰이 유효하지 않습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U006", "존재하지 않는 사용자입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
