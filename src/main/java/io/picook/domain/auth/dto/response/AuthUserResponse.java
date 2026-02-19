package io.picook.domain.auth.dto.response;

public record AuthUserResponse(
        Long id,
        String nickname,
        String email,
        String profileImage
) {
}
