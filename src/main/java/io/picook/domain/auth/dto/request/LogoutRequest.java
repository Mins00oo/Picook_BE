package io.picook.domain.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequest(
        @NotBlank(message = "refreshToken is required")
        String refreshToken
) {
}
