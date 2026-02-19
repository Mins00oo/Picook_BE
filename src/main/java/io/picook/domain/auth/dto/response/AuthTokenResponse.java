package io.picook.domain.auth.dto.response;

public record AuthTokenResponse(
        String accessToken,
        String refreshToken,
        AuthUserResponse user
) {
}
