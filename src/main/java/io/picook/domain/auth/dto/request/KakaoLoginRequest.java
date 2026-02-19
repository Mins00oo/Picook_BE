package io.picook.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record KakaoLoginRequest(
        @NotBlank(message = "accessToken is required")
        String accessToken
) {
}
